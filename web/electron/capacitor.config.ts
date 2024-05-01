import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.purview',
  appName: 'jmet-purview',
  webDir: '../target/classes/static/',
  plugins: {
    "Electron": {
      "path": "./node_modules/@capacitor-community/electron"
    }
  }
};

export default config;
