import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [
  // {
  //   name: 'Dashboard',
  //   url: '/dashboard',
  //   iconComponent: { name: 'cil-speedometer' },
  //   attributes: {
  //     roles: 'ROLE_OPM'
  //   }
  // },
  // {
  //   title: true,
  //   name: 'Request',
  //   attributes: {
  //     roles: 'ROLE_OPM'
  //   }
  // },
  // {
  //   name: 'Create Request',
  //   url: '/request/create',
  //   iconComponent: { name: 'cil-notes' },
  //   attributes: {
  //     roles: 'ROLE_OPM'
  //   }
  // },
  // {
  //   name: 'Search Request',
  //   url: '/request/search',
  //   iconComponent: { name: 'cil-magnifying-glass' },
  //   attributes: {
  //     roles: 'ROLE_OPM'
  //   }
  // },
  // {
  //   title: true,
  //   name: 'Admin',
  //   attributes: {
  //     roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM'
  //   }
  // },
  {
    name: 'Dashboard',
    url: '/admin/dashboard',
    iconComponent: { name: 'cil-chart' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS'
    }
  },
  {
    name: 'Approvals',
    url: '/admin/approvals',
    iconComponent: { name: 'cil-thumb-up' },
    attributes: {
      roles: 'ROLE_SRE'
    }
  },
  {
    name: 'Sites',
    url: '/admin/sites',
    iconComponent: { name: 'cil-factory' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS'
    }
  },
  {
    name: 'Apps',
    url: '/admin/apps',
    iconComponent: { name: 'cib-app-store' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS'
    }
  },
  {
    name: 'Products',
    url: '/admin/products',
    iconComponent: { name: 'cil-devices' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM'
    }
  },
  {
    name: 'Site-Product Support',
    url: '/admin/site-product',
    iconComponent: { name: 'cil-task' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM'
    }
  },
  {
    name: 'Product Readiness',
    url: '/admin/product-readiness',
    iconComponent: { name: 'cil-spreadsheet' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM'
    }
  },
  {
    name: 'Features',
    url: '/admin/features',
    iconComponent: { name: 'cil-layers' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM'
    }
  },
  {
    name: 'Site-Feature Support',
    url: '/admin/site-feature',
    iconComponent: { name: 'cil-task' },
    attributes: {
      roles: 'ROLE_SRE,ROLE_OPS,ROLE_EPM'
    }
  },
  {
    name: 'Users',
    url: '/admin/users',
    iconComponent: { name: 'cib-myspace' },
    attributes: {
      roles: 'ROLE_SRE'
    }
  },
];
