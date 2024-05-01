package com.apple.jmet.purview.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.apple.jmet.purview.domain.Action;
import com.apple.jmet.purview.domain.App;
import com.apple.jmet.purview.domain.AppConfig;
import com.apple.jmet.purview.domain.Feature;
import com.apple.jmet.purview.domain.FeatureApp;
import com.apple.jmet.purview.domain.GenericConfig;
import com.apple.jmet.purview.domain.Person;
import com.apple.jmet.purview.domain.ProductApp;
import com.apple.jmet.purview.domain.ProductFeature;
import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.domain.SiteApp;
import com.apple.jmet.purview.domain.SiteAppConfig;
import com.apple.jmet.purview.domain.SiteFeature;
import com.apple.jmet.purview.domain.SiteProduct;
import com.apple.jmet.purview.domain.Task;
import com.apple.jmet.purview.dto.ActionDto;
import com.apple.jmet.purview.dto.RequestDto;
import com.apple.jmet.purview.dto.RequestListDto;
import com.apple.jmet.purview.dto.TaskDto;
import com.apple.jmet.purview.enums.ActionStatus;
import com.apple.jmet.purview.enums.Environment;
import com.apple.jmet.purview.enums.RequestStatus;
import com.apple.jmet.purview.enums.RolloutPhase;
import com.apple.jmet.purview.integration.ReferenceGenerator;
import com.apple.jmet.purview.listeners.GenerateActionsListener;
import com.apple.jmet.purview.listeners.RequestStatePublisher;
import com.apple.jmet.purview.repository.ActionRepository;
import com.apple.jmet.purview.repository.AppConfigValueRepository;
import com.apple.jmet.purview.repository.FeatureRepository;
import com.apple.jmet.purview.repository.PersonRepository;
import com.apple.jmet.purview.repository.ProductFeatureRepository;
import com.apple.jmet.purview.repository.ProductRepository;
import com.apple.jmet.purview.repository.RequestListDtoRepository;
import com.apple.jmet.purview.repository.RequestRepository;
import com.apple.jmet.purview.repository.SiteAppRepository;
import com.apple.jmet.purview.repository.SiteFeatureRepository;
import com.apple.jmet.purview.repository.SiteProductRepository;
import com.apple.jmet.purview.repository.SiteRepository;
import com.apple.jmet.purview.repository.TaskRepository;
import com.apple.jmet.purview.utils.ComparisonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.Request}.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Transactional
public class RequestResource {

    private static final String MESSAGE_404 = "Request not found or not authorised";
    private static final String MESSAGE_401 = "Unauthorised action";

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReferenceGenerator referenceGenerator;
    @Autowired
    private GenerateActionsListener genActionService;
    @Autowired
    private SiteProductRepository siteProductRepository;
    @Autowired
    private SiteFeatureRepository siteFeatureRepository;
    @Autowired
    private SiteAppRepository siteAppRepository;
    @Autowired
    private AppConfigValueRepository appConfigValueRepository;
    @Autowired
    private RequestListDtoRepository requestListDtoRepository;
    @Autowired
    private ProductFeatureRepository productFeatureRepository;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private PersonResource personResource;
    @Autowired
    private ActionRepository actionRepository;
    @Autowired
    private TaskRepository taskRepository;
    
    private RequestStatePublisher publisher = RequestStatePublisher.getInstance();
    private ComparisonUtils comparisonUtils = ComparisonUtils.getInstance();

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_EPM')")
    @PostMapping("/requests")
    public Request createRequest(@RequestBody Request request) {
        
        request.setReferenceId(this.referenceGenerator.createTicket(null));
        request.setRequestStatus(RequestStatus.OPEN);

        request.setRequestor(this.personRepository.findById(request.getUserId()).orElse(null));
        request.setSite(this.siteRepository.findById(request.getSiteIdToSave()).orElse(null));
        request.setProduct(this.productRepository.findById(request.getProductIdToSave()).orElse(null));
        request.setFeature(this.featureRepository.findById(request.getFeatureIdToSave()).orElse(null));

        publisher.openRequest(request);
        genActionService.update(RequestStatus.OPEN, request);
        
        request.incrementVersion();
        return requestRepository.save(request);
    }

    private void completeRequest(Request request) {
        request.setRequestStatus(RequestStatus.COMPLETED);
        request.getActions().forEach(action -> {

            RolloutPhase resultingRolloutPhase;
            if (StringUtils.equalsIgnoreCase(request.getSite().getCode(), "DECK")) {
                resultingRolloutPhase = RolloutPhase.DECK;
            } else {
                resultingRolloutPhase = RolloutPhase.valueOf(action.getEnvironment());
            }

            action.getTasks().forEach(task -> {
                if (task.getAppConfig().isMulti()) {
                    List<String> values = Arrays.asList(task.getAppConfigSetValue().split(","));
                    this.appConfigValueRepository.updateRolloutPhaseBulk(resultingRolloutPhase.getValue(), values,
                            task.getAppConfig().getId());
                } else {
                    this.appConfigValueRepository.updateRolloutPhase(resultingRolloutPhase.getValue(),
                            task.getAppConfigSetValue(), task.getAppConfig().getId());
                }
            });
        });

        RolloutPhase resultingRolloutPhase;
        boolean anyProdAction = request.getActions().stream()
                .anyMatch(a -> StringUtils.equalsAnyIgnoreCase(a.getEnvironment(), Environment.PRODUCTION.getValue()));
        if (StringUtils.equalsIgnoreCase(request.getSite().getCode(), "DECK")) {
            resultingRolloutPhase = RolloutPhase.DECK;
        } else {
            resultingRolloutPhase = anyProdAction ? RolloutPhase.PRODUCTION : RolloutPhase.STAGING;
        }

        this.updateProductFeatures(request);
        this.updateFeaturePhase(request);
        this.updateSiteProduct(request, resultingRolloutPhase);
        this.updateSiteFeature(request, resultingRolloutPhase);

        request.setCompletedDate(new Date());
    }

    private void updateProductFeatures(Request request){
        if(request.getProduct() != null){
            List<ProductFeature> productFeatures = this.productFeatureRepository
                    .findByProductId(request.getProduct().getId());
            RolloutPhase resultingRolloutPhase;
            boolean anyProdAction = request.getActions().stream()
                    .anyMatch(a -> StringUtils.equalsAnyIgnoreCase(a.getEnvironment(), Environment.PRODUCTION.getValue()));
            if (StringUtils.equalsIgnoreCase(request.getSite().getCode(), "DECK")) {
                resultingRolloutPhase = RolloutPhase.DECK;
            } else {
                resultingRolloutPhase = anyProdAction ? RolloutPhase.PRODUCTION : RolloutPhase.STAGING;
            }
            productFeatures.forEach(pf -> {
                Feature feature = pf.getFeature();
                feature.promoteConfig(resultingRolloutPhase);
                this.featureRepository.save(feature);
            });
        }
    }

    private void updateSiteProduct(Request request, RolloutPhase resultingRolloutPhase){
        if(request.getProduct() != null){
            Optional<SiteProduct> existingSpOpt = this.siteProductRepository
                .findBySiteIdAndProductId(request.getSite().getId(), request.getProduct().getId());

            SiteProduct sp;
            if (existingSpOpt.isPresent()) {
                sp = existingSpOpt.get();
                sp.setDateSupported(new Date());
                sp.setEnvironment(resultingRolloutPhase.getValue());
            } else {
                sp = SiteProduct.builder()
                        .requestReferenceId(request.getReferenceId())
                        .site(request.getSite())
                        .product(request.getProduct())
                        .dateSupported(new Date())
                        .environment(resultingRolloutPhase.getValue())
                        .build();
            }
            this.siteProductRepository.save(sp);
        }
    }

    private void updateSiteFeature(Request request, RolloutPhase resultingRolloutPhase){
        if(request.getFeature() != null){
            Optional<SiteFeature> existingSfOpt = this.siteFeatureRepository
                .findBySiteIdAndFeatureId(request.getSite().getId(), request.getFeature().getId());

            SiteFeature sf;
            if (existingSfOpt.isPresent()) {
                sf = existingSfOpt.get();
                sf.setDateSupported(new Date());
                sf.setEnvironment(resultingRolloutPhase.getValue());
            } else {
                sf = SiteFeature.builder()
                        .requestReferenceId(request.getReferenceId())
                        .site(request.getSite())
                        .feature(request.getFeature())
                        .dateSupported(new Date())
                        .environment(resultingRolloutPhase.getValue())
                        .build();
            }
            this.siteFeatureRepository.save(sf);
        }
    }

    private void updateFeaturePhase(Request request){
        if(request.getFeature() != null){
            Feature feature = request.getFeature();
            RolloutPhase resultingRolloutPhase;
            boolean anyProdAction = request.getActions().stream()
                    .anyMatch(a -> StringUtils.equalsAnyIgnoreCase(a.getEnvironment(), Environment.PRODUCTION.getValue()));
            if (StringUtils.equalsIgnoreCase(request.getSite().getCode(), "DECK")) {
                resultingRolloutPhase = RolloutPhase.DECK;
            } else {
                resultingRolloutPhase = anyProdAction ? RolloutPhase.PRODUCTION : RolloutPhase.STAGING;
            }
            feature.promoteConfig(resultingRolloutPhase);
            this.featureRepository.save(feature);
        }
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_OPS','ROLE_EPM')")
    @PutMapping("/requests/{id}")
    public Request updateRequest(@PathVariable(value = "id", required = false) final Long id,
            @RequestBody Request request) {
        Optional<Request> requestToEditOpt = this.requestRepository.findById(id);
        if (requestToEditOpt.isPresent()) {
            Request requestToEdit = requestToEditOpt.get();
            requestToEdit.setDescription(request.getDescription());
            requestToEdit.setNeedByDate(request.getNeedByDate());
            requestToEdit.setRequestStatus(RequestStatus.valueOf(request.getStatus()));
            return requestRepository.save(requestToEdit);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MESSAGE_404);
        }
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PutMapping("/requests-cancel")
    public Request cancelRequestByDetails(@RequestParam String siteCode, @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String featureName) {
        
        List<Request> requests = new ArrayList<>();
        if(StringUtils.isNotBlank(productCode)){
            requests = requestRepository.findBySiteCodeAndProductCode(siteCode, productCode);
        }else if(StringUtils.isNotBlank(featureName)){
            requests = requestRepository.findBySiteCodeAndFeatureName(siteCode, featureName);
        }

        if (CollectionUtils.isNotEmpty(requests)) {
            requests.stream().forEach(request -> {
                request.getActions().stream().forEach(action -> {
                    action.setActionStatus(ActionStatus.CANCELLED);
                    action.getTasks().stream().forEach(task ->
                        task.setTaskStatus(ActionStatus.CANCELLED)
                    );
                });
                request.setRequestStatus(RequestStatus.CANCELLED);
                this.siteProductRepository.deleteByRequestReferenceId(request.getReferenceId());
                this.siteFeatureRepository.deleteByRequestReferenceId(request.getReferenceId());
                this.requestRepository.save(request);
            });
            return requests.stream().findAny().get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MESSAGE_404);
        }
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE','ROLE_OPS','ROLE_EPM')")
    @PutMapping("/requests-update-actions/{id}")
    public Request updateRequestActions(@PathVariable(value = "id", required = false) final Long id,
            @RequestBody Request request) {
        Optional<Request> requestToEditOpt = this.requestRepository.findById(id);
        if (requestToEditOpt.isPresent()) {
            Request requestToEdit = requestToEditOpt.get();
            
            requestToEdit.setNeedByDate(request.getNeedByDate());
            requestToEdit.setStatus(request.getStatus());
            if (request.getStatus().equals(RequestStatus.COMPLETED.getValue())) {
                completeRequest(requestToEdit);
            }
            request.getActions().forEach(action -> {
                action.setRequest(requestToEdit);
                this.updateSimilarActionStatus(requestToEdit, action);
                action.getTasks().forEach(task -> {
                    task.setAction(action);
                    if (!RequestStatus.COMPLETED.getValue().equals(task.getStatus())){
                        task.setCompletedDate(null);
                    }
                    if (RequestStatus.COMPLETED.getValue().equals(task.getStatus())
                            && task.getCompletedDate() == null) {
                        task.setCompletedDate(new Date());
                        this.updateSimilarTaskStatus(requestToEdit, task);
                    }
                });
                if(RequestStatus.COMPLETED.getValue().equals(action.getStatus())){
                    this.updateSiteAppConfigs(requestToEdit, action);
                }
            });
            requestToEdit.setActions(request.getActions());
            requestToEdit.setVersion(request.getVersion());
            return requestRepository.save(requestToEdit);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MESSAGE_404);
        }
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PutMapping("/requests-update-action-verification/{id}")
    public Action updateActionVerification(@PathVariable(value = "id", required = false) final Long actionId,
            @RequestBody Action action) {
        Person currentUser = this.personResource.getCurrentUserInternally();
        Optional<Action> actionToEditOpt = this.actionRepository.findById(actionId);
        Action actionToEdit = actionToEditOpt.orElse(null);
        if (actionToEdit != null && 
                (actionToEdit.getApp().getSre().getId() == currentUser.getId()
                    || actionToEdit.getApp().getBackupSres().stream().anyMatch(sre -> sre.getId() == currentUser.getId()))) {
            actionToEdit.updateVerifiedInfo(action.isVerified(), currentUser.getEmail());
            if(action.isVerified()){
                //propagate approval only
                actionToEdit.setVerifier(currentUser);
                this.updateSimilarActionVerification(actionToEdit);
            }
            return this.actionRepository.save(actionToEdit);
        } else {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        }
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PutMapping("/requests-update-action-ignore/{id}")
    public Action updateActionIgnore(@PathVariable(value = "id", required = false) final Long actionId,
            @RequestBody Action action) {
        Person currentUser = this.personResource.getCurrentUserInternally();
        Optional<Action> actionToEditOpt = this.actionRepository.findById(actionId);
        Action actionToEdit = actionToEditOpt.orElse(null);
        if (actionToEdit != null && 
                (actionToEdit.getApp().getSre().getId() == currentUser.getId()
                    || actionToEdit.getApp().getBackupSres().stream().anyMatch(sre -> sre.getId() == currentUser.getId()))) {
            actionToEdit.setToIgnore(action.isToIgnore());
            return this.actionRepository.save(actionToEdit);
        } else {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        }
    }

    private void updateSimilarActionVerification(Action action){
        List<Action> similarAppActions = this.actionRepository.findSimilarUnapprovedActionsExceptThis(action.getId(), action.getApp().getId());
        List<Action> filteredSimilarActions = this.filterSimilarActionsBasedOnConfig(similarAppActions, action);
        if(CollectionUtils.isNotEmpty(filteredSimilarActions)){
            filteredSimilarActions.forEach(similarAction -> {
                similarAction.updateVerifiedInfo(action.isVerified(), action.getVerifiedBy());
                this.actionRepository.save(similarAction);
            });
        }
    }

    private void updateSimilarActionStatus(Request requestToEdit, Action action){
        List<Request> sameSiteRequests = this.requestRepository.findBySiteIdAndIdNot(requestToEdit.getSite().getId(), requestToEdit.getId());
        List<Action> similarActions = sameSiteRequests.stream().flatMap(r -> r.getActions().stream()).collect(Collectors.toList());
        List<Action> filteredSimilarActions = this.filterSimilarActionsBasedOnConfig(similarActions, action);
        filteredSimilarActions.forEach(similarAction -> {
            similarAction.updateCrAndRadarIds(action);
            similarAction.setStatus(action.getStatus());
            this.actionRepository.save(similarAction);

            Request requestToCheck = this.requestRepository.findById(similarAction.getRequest().getId()).get();
            Set<String> actionStatuses = requestToCheck.getActions().stream().map(Action::getStatus).collect(Collectors.toSet());
            if(actionStatuses.size() == 1 && RequestStatus.COMPLETED.getValue().equals(actionStatuses.iterator().next())){
                completeRequest(requestToCheck);
            }
        });
    }

    private List<Action> filterSimilarActionsBasedOnConfig(List<Action> similarActions, Action action) {
        return similarActions.stream().filter(a -> {
            int matchingTasks = 0;
            for (Task task : action.getTasks()) {
                for (Task otherTask : a.getTasks()) {
                    if (task.getAppConfig().getId() == otherTask.getAppConfig().getId()
                            && this.isValueQualified(task, otherTask)) {
                        matchingTasks++;
                    }
                }
            }
            return matchingTasks == action.getTasks().size();
        }).collect(Collectors.toList());
    }

    private void updateSimilarTaskStatus(Request requestToEdit, Task task){
        List<Task> similarTasks = this.getSimilarOpenTasks(requestToEdit, task);
        similarTasks.forEach(similarTask -> {
            similarTask.setStatus(task.getStatus());
            similarTask.setCompletedDate(task.getCompletedDate());
            this.taskRepository.save(similarTask);
            Action action = similarTask.getAction();
            Optional<Task> anyPendingTaskInAction = action.getTasks().stream().filter(t -> !t.getStatus().equals(ActionStatus.COMPLETED.getValue())).findFirst();
            if(anyPendingTaskInAction.isEmpty()){
                action.setActionStatus(ActionStatus.COMPLETED);
                this.actionRepository.save(action);
            }
        });
    }

    private List<Task> getSimilarOpenTasks(Request request, Task task) {
        List<Request> sameSiteRequests = this.requestRepository.findBySiteIdAndIdNot(request.getSite().getId(), request.getId());
        return sameSiteRequests.stream().flatMap(r -> r.getActions().stream().flatMap(a -> a.getTasks().stream())).filter(t -> {
            return !RequestStatus.COMPLETED.getValue().equals(t.getStatus())
                            && t.getAppConfig().getId() == task.getAppConfig().getId()
                            && this.isValueQualified(task, t);
        }).collect(Collectors.toList());
    }

    private boolean isValueQualified(Task task, Task otherTask){
        task.setAppConfigSetValue(StringUtils.defaultString(task.getAppConfigSetValue()));
        otherTask.setAppConfigSetValue(StringUtils.defaultString(otherTask.getAppConfigSetValue()));

        if(task.getAppConfig().isMulti()){
            Set<String> taskVals = new HashSet<>(Arrays.asList(task.getAppConfigSetValue().split(",")));
            Set<String> otherTaskVals = new HashSet<>(Arrays.asList(otherTask.getAppConfigSetValue().split(",")));
            return taskVals.containsAll(otherTaskVals);
        }else {
            return comparisonUtils.singleValueCompare(task.getAppConfigSetValue(), otherTask.getAppConfigSetValue()) >= 0;
        }
    }

    private void updateSiteAppConfigs(Request request, Action action){
        final SiteApp siteApp = this.siteAppRepository
                .findBySiteIdAndAppIdAndEnvironment(request.getSite().getId(), action.getApp().getId(),
                        action.getEnvironment())
                .orElse(SiteApp.builder()
                        .site(request.getSite())
                        .app(action.getApp())
                        .environment(action.getEnvironment())
                        .siteAppConfigs(new ArrayList<>())
                        .build());

        action.getTasks().forEach(task -> {
            SiteAppConfig siteAppConfig = siteApp.getSiteAppConfigs().stream()
                    .filter(sac -> StringUtils.equalsIgnoreCase(sac.getName(), task.getAppConfig().getName()))
                    .findFirst().orElse(null);
            if (siteAppConfig == null) {
                siteAppConfig = SiteAppConfig.builder()
                        .name(task.getAppConfig().getName())
                        .multi(task.getAppConfig().isMulti())
                        .dateAdded(new Date())
                        .val(task.getAppConfigSetValue())
                        .build();
                siteApp.getSiteAppConfigs().add(siteAppConfig);
            } else {
                if (task.getAppConfig().isMulti()) {
                    Set<String> taskVals = new HashSet<>(Arrays.asList(task.getAppConfigSetValue().split(",")));
                    taskVals.addAll(Arrays.asList(siteAppConfig.getVal().split(",")));
                    siteAppConfig.setVal(String.join(",", taskVals));
                } else if (StringUtils.isBlank(siteAppConfig.getVal())
                        || comparisonUtils.singleValueCompare(siteAppConfig.getVal(), task.getAppConfigSetValue()) < 0) {
                    siteAppConfig.setVal(task.getAppConfigSetValue());
                }
            }
        });

        this.siteAppRepository.save(siteApp);
    }

    @GetMapping("/requests/mine")
    public List<Request> getMyRequests() {
        Person currentUser = this.personResource.getCurrentUserInternally();
        if (currentUser == null) {
            return List.of();
        }
        return requestRepository.findByRequestorId(currentUser.getId());
    }

    @GetMapping("/requests-list-dto/mine")
    public List<RequestListDto> getMyRequestsListDto() {
        Person currentUser = this.personResource.getCurrentUserInternally();
        if (currentUser == null) {
            return List.of();
        }
        return requestListDtoRepository.findAllByRequestorId(currentUser.getId());
    }

    @GetMapping("/requests-status-count-all")
    public Integer getAllRequestsCountByStatus(@RequestParam(name = "status", required = true) String status) {
        return this.requestRepository.countByStatus(status);
    }

    @GetMapping("/requests-status-count-mine")
    public Integer getRequestsCountByStatusAndRequestor(@RequestParam(name = "status", required = true) String status) {
        Person currentUser = this.personResource.getCurrentUserInternally();
        if (currentUser == null) {
            return 0;
        }
        return this.requestRepository.countByStatusAndRequestorId(status, currentUser.getId());
    }

    @GetMapping("/requests")
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @GetMapping("/requests-list-dto/products")
    public List<RequestListDto> getAllProductRequestsListDto() {
        return requestListDtoRepository.findAllByProductIdNotNull();
    }

    @GetMapping("/requests-list-dto/features")
    public List<RequestListDto> getAllFeatureRequestsListDto() {
        return requestListDtoRepository.findAllByFeatureIdNotNull();
    }

    @GetMapping("/requests/{id}")
    public Request getRequest(@PathVariable Long id) {
        Optional<Request> reqOpt = requestRepository.findById(id);
        if (reqOpt.isPresent()) {
            return reqOpt.get();
        } else {
            return null;
        }
    }

    @GetMapping("/requests/ref/{referenceId}")
    public Request getRequest(@PathVariable String referenceId) {
        Optional<Request> reqOpt = requestRepository.findByReferenceId(referenceId);   
        if (reqOpt.isPresent()) {
            Request request = reqOpt.get();
            request.getActions().parallelStream().filter(Action::isVerified).forEach(action -> {
                action.setVerifier(this.personRepository.findByEmail(action.getVerifiedBy()).orElse(null));
            });
            return request;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MESSAGE_404);
        }
    }

    @GetMapping("/requests-dto/ref/{referenceId}")
    public RequestDto getRequestDto(@PathVariable String referenceId) {
        Optional<Request> reqOpt = requestRepository.findByReferenceId(referenceId);   
        if (reqOpt.isPresent()) {
            Request request = reqOpt.get();
            return extractDto(request);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MESSAGE_404);
        }
    }

    @GetMapping("/request-by-details")
    public Request getRequestByDetails(@RequestParam String siteCode, @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String featureName) {
        
        Optional<Request> reqOpt = Optional.empty();
        if(StringUtils.isNotBlank(productCode)){
            reqOpt = requestRepository.findBySiteCodeAndProductCode(siteCode, productCode)
                        .stream().filter(Request::notCancelledNorRejected).findFirst();
        }else if(StringUtils.isNotBlank(featureName)){
            reqOpt = requestRepository.findBySiteCodeAndFeatureName(siteCode, featureName)
                        .stream().filter(Request::notCancelledNorRejected).findFirst();
        }

        if (reqOpt.isPresent()) {
            Request request = reqOpt.get();
            request.getActions().parallelStream().filter(Action::isVerified).forEach(action -> {
                action.setVerifier(this.personRepository.findByEmail(action.getVerifiedBy()).orElse(null));
            });
            return request;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MESSAGE_404);
        }
    }

    @GetMapping("/request-by-details-dto")
    public RequestDto getRequestDtoByDetails(@RequestParam String siteCode, @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String featureName) {
        
        Optional<Request> reqOpt = Optional.empty();
        if(StringUtils.isNotBlank(productCode)){
            reqOpt = requestRepository.findBySiteCodeAndProductCode(siteCode, productCode)
                        .stream().filter(Request::notCancelledNorRejected).findFirst();
        }else if(StringUtils.isNotBlank(featureName)){
            reqOpt = requestRepository.findBySiteCodeAndFeatureName(siteCode, featureName)
                        .stream().filter(Request::notCancelledNorRejected).findFirst();
        }

        if (reqOpt.isPresent()) {
            Request request = reqOpt.get();
            return extractDto(request);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MESSAGE_404);
        }
    }

    private RequestDto extractDto(Request request){
        ModelMapper mapper = new ModelMapper();
        RequestDto requestDto = mapper.map(request, RequestDto.class);
        requestDto.setExportDate(new Date());
        requestDto.getActions().forEach(action -> {
            Collections.sort(action.getTasks(), Comparator.comparing(TaskDto::getAppConfigName));
            action.getTasks().forEach(task -> {
                if(task.isAppConfigMulti()){
                    List<String> values = Arrays.asList(task.getAppConfigSetValue().split(","));
                    values.sort(String::compareTo);
                    task.setAppConfigSetValue(String.join(",", values));
                }
            });
        });
        Collections.sort(requestDto.getActions(), Comparator.comparing(ActionDto::getAppInternalName));
        return requestDto;
    }

    public List<Request> updateRequestActionsBasedOnProductChanges(@PathVariable Long productId,
            List<ProductApp> productApps) {
        List<Request> reqToUpdate = this.requestRepository.findByProductId(productId);
        reqToUpdate.stream().forEach(req -> {
            productApps.stream().forEach(pa -> {
                List<Action> exActionList = req.getActions().stream()
                        .filter(ra -> ra.getApp().getId() == pa.getApp().getId()).toList();
                if (CollectionUtils.isNotEmpty(exActionList)) {
                    exActionList.stream().forEach(action -> {

                        log.debug("Product App Configs in Request Process: {}", pa.getProductAppConfigs().stream().map(pac -> pac.getName()).collect(Collectors.toSet()));

                        pa.getProductAppConfigs().forEach(pac -> 
                            this.evaluateActionTasks(action, pa.getApp(), pac, req)
                        );
                        List<Task> tasksToDelete = action.getTasks().stream()
                            .filter(task -> !pa.getProductAppConfigs().stream().map(pac -> pac.getName()).collect(Collectors.toSet())
                            .contains(task.getAppConfig().getName())).toList();
                        if(CollectionUtils.isNotEmpty(tasksToDelete)){
                            log.debug("Deleting tasks: {}", tasksToDelete.stream().map(task -> task.getAppConfig().getName()).collect(Collectors.toList()));
                            action.getTasks().removeAll(tasksToDelete);
                        }
                    });
                    req.getActions().removeIf(action -> action.getTasks().isEmpty());
                } else {
                    Action action = this.genActionService.buildAction(req, pa.getApp(), pa.getProductAppConfigs());
                    if(action != null){
                        req.getActions().add(action);
                    }
                }
            });

            //delete unset app actions
            List<Action> actionsToRemove = req.getActions().stream()
                    .filter(action -> productApps.stream().noneMatch(pa -> pa.getApp().getId() == action.getApp().getId()))
                    .collect(Collectors.toList());
            req.getActions().removeAll(actionsToRemove);
            
            if(req.getActions().stream().anyMatch(action -> !StringUtils.equalsIgnoreCase(ActionStatus.COMPLETED.getValue(), action.getStatus()))){
                req.setRequestStatus(RequestStatus.REVIEW);
                req.setCompletedDate(null);
            }

            req.incrementVersion();
            this.requestRepository.save(req);
        });
        return reqToUpdate;
    }

    public List<Request> updateRequestActionsBasedOnFeatureChanges(@PathVariable Long featureId,
            List<FeatureApp> featureApps) {
        List<Request> reqToUpdate = this.requestRepository.findByFeatureId(featureId);
        reqToUpdate.stream().forEach(req -> {
            featureApps.stream().forEach(fa -> {
                List<Action> exActionList = req.getActions().stream()
                        .filter(ra -> ra.getApp().getId() == fa.getApp().getId()).toList();
                if (CollectionUtils.isNotEmpty(exActionList)) {
                    exActionList.stream().forEach(action -> 
                        fa.getFeatureAppConfigs().forEach(fac -> 
                            this.evaluateActionTasks(action, fa.getApp(), fac, req)
                        )
                    );
                } else {
                    Action action = this.genActionService.buildAction(req, fa.getApp(), fa.getFeatureAppConfigs());
                    if(action != null){
                        req.getActions().add(action);
                    }
                }
            });

            //delete unset app actions
            List<Action> actionsToRemove = req.getActions().stream()
                    .filter(action -> featureApps.stream().noneMatch(fa -> fa.getApp().getId() == action.getApp().getId()))
                    .collect(Collectors.toList());
            req.getActions().removeAll(actionsToRemove);

            req.incrementVersion();
            this.requestRepository.save(req);
        });
        return reqToUpdate;
    }

    private void evaluateActionTasks(Action action, App app, GenericConfig gc, Request req){
        List<Task> matchingTasksToUpdate = action.getTasks().stream().filter(
                task -> StringUtils.equalsIgnoreCase(task.getAppConfig().getName(), gc.getName()))
                .collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(matchingTasksToUpdate)){
            matchingTasksToUpdate.stream().filter(task -> task.getAppConfig().isMulti()).forEach(multiValueTask -> {
                List<String> taskVals = Arrays.asList(multiValueTask.getAppConfigSetValue().split(","));
                List<String> gcVals = Arrays.asList(gc.getVal().split(","));
                taskVals.sort(String::compareTo);
                gcVals.sort(String::compareTo);
                multiValueTask.setAppConfigSetValue(taskVals.stream().distinct().collect(Collectors.joining(",")));
                gc.setVal(gcVals.stream().distinct().collect(Collectors.joining(",")));
            });
        }
        
        List<Task> matchingTasksWithDiffVal = matchingTasksToUpdate.stream().filter(
                task -> !StringUtils.equalsIgnoreCase(task.getAppConfigSetValue(), gc.getVal()))
                .collect(Collectors.toList());

        if (matchingTasksToUpdate.isEmpty() && StringUtils.isNotBlank(gc.getVal())) { // no task found for this app config
            AppConfig ac = app.getAppConfigs().stream()
                .filter(acFilter -> StringUtils.equalsIgnoreCase(acFilter.getName(), gc.getName()))
                .findFirst().orElse(null);
            Task task = Task.builder()
                .action(action)
                .appConfig(ac)
                .appConfigSetValue(gc.getVal())
                .status(ActionStatus.OPEN.getValue())
                .assignee(app.getSre())
                .build();
            action.getTasks().add(task);
            action.updateVerifiedInfo(false, null);
            action.setStatus(ActionStatus.REVIEW.getValue());
        } else if (!matchingTasksWithDiffVal.isEmpty() && StringUtils.isNotBlank(gc.getVal())) { // task found but with different value
            matchingTasksWithDiffVal.forEach(task -> {
                task.setStatus(ActionStatus.REVIEW.getValue());
                task.setCompletedDate(null);
                this.updateTaskSetValue(task, gc);
            });
        }
    }

    private Task updateTaskSetValue(Task task, GenericConfig gc) {
        if (task.getAppConfig().isMulti()) {
            Set<String> taskVals = new HashSet<>(Arrays.asList(task.getAppConfigSetValue().split(",")));
            Set<String> gcVals = new HashSet<>(Arrays.asList(gc.getVal().split(",")));
            taskVals.addAll(gcVals);
            Set<String> valsToRemove = taskVals.stream().filter(taskVal -> !gcVals.contains(taskVal)).collect(Collectors.toSet());
            if(CollectionUtils.isNotEmpty(valsToRemove)){
                log.debug("Deleting values for [{}]: {}", gc.getName(), valsToRemove);
                taskVals.removeAll(valsToRemove);
            }
            task.setAppConfigSetValue(String.join(",", taskVals));
            task.getAction().updateVerifiedInfo(false, null);
            task.getAction().setStatus(ActionStatus.REVIEW.getValue());
        } else if(task.getAppConfig().isRecommend()){
            String previousValue = task.getAppConfigSetValue();
            String recommendedValue = this.genActionService.getRecommendedVersion(task.getAction().getRequest(), task.getAction(), task.getAppConfig());
            if(comparisonUtils.singleValueCompare(recommendedValue, gc.getVal()) > 0){
                task.setAppConfigSetValue(recommendedValue);
            }else{
                task.setAppConfigSetValue(gc.getVal());
            }
            if(!StringUtils.equalsIgnoreCase(previousValue, task.getAppConfigSetValue())){
                task.getAction().updateVerifiedInfo(false, null);
                task.getAction().setStatus(ActionStatus.REVIEW.getValue());
            }
        }else{
            task.setAppConfigSetValue(gc.getVal());
            task.getAction().updateVerifiedInfo(false, null);
            task.getAction().setStatus(ActionStatus.REVIEW.getValue());
        }
        return task;
    }

    public List<Request> addRequestActionsForMp(@PathVariable Long productId,
            List<ProductApp> productApps) {
        List<Request> requestsToUpdate = this.requestRepository.findByProductId(productId);
        requestsToUpdate.stream().forEach(req -> {
            productApps.stream().forEach(pa -> {
                Optional<Action> exMpActionOpt = req.getActions().stream()
                        .filter(ra -> ra.getApp().getId() == pa.getApp().getId() && ra.isForProduction()).findFirst();
                if (!exMpActionOpt.isPresent()) {
                    Action action = this.genActionService.buildAction(req, pa.getApp(), pa.getProductAppConfigs());
                    if(action != null){
                        req.getActions().add(action);
                        req.setRequestStatus(RequestStatus.REVIEW);
                    }
                }
            });
            req.incrementVersion();
            this.requestRepository.save(req);
        });
        return requestsToUpdate;
    }

    @GetMapping("/requests-pending-actions")
    public List<Action> getAllPendingActions() {
        Person currentPerson = this.personResource.getCurrentUserInternally();
        List<Action> pendingActions = actionRepository.findAllByVerified(false).stream()
            .filter(action -> action.getApp().getSre().getId() == currentPerson.getId() 
                        && !action.isToIgnore()
                        && !StringUtils.equalsAnyIgnoreCase(action.getRequest().getStatus(), RequestStatus.CANCELLED.getValue(), RequestStatus.REJECTED.getValue()))
            .collect(Collectors.toList());
        pendingActions.forEach(action -> {
            action.setSite(action.getRequest().getSite());
            action.setProduct(action.getRequest().getProduct());
            action.setFeature(action.getRequest().getFeature());
        });
        return pendingActions;
    }

    @GetMapping("/requests-by-products")
    public List<Request> getRequestByProducts(@RequestParam String productCodes) {
        
        List<String> productCodeList = Arrays.asList(productCodes.split(","));
        List<String> excludeStatus = List.of(RequestStatus.CANCELLED.getValue(), RequestStatus.REJECTED.getValue());
        return this.requestRepository.findByProductCodeInAndStatusNotIn(productCodeList, excludeStatus);

    }
}
