export const environment = {
  production: false,
  api: '${{ secrets.backend.api.url }}',
  oidc: {
    authority: '${{ secrets.oidc.url }}',
    clientId: '${{ secrets.oidc.jwks.client.id }}',
    redirectUri: window.location.origin + '/login-callback', // Redirect URI after login
    responseType: 'code', // Response type (code for Authorization Code Flow)
    scope: 'openid profile email', // Scopes requested during authentication
    postLogoutRedirectUri: window.location.origin + '/logout-callback', // Redirect URI after logout
    automaticSilentRenew: true, // Enable automatic silent token renewal
    silentRenewUrl: window.location.origin + '/silent-callback.html' // Silent renewal URL
  },
  roles: [
    {
      "code": "SRE",
      "groupId": "${{ secrets.role.sre.group.id }}"
    }, {
      "code": "EPM",
      "groupId": "${{ secrets.role.epm.group.id }}"
    }, {
      "code": "OPS",
      "groupId": "${{ secrets.role.ops.group.id }}"
    }, {
      "code": "APS",
      "groupId": "${{ secrets.role.aps.group.id }}"
    }
  ]
};
