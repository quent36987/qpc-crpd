import { defineConfig } from "cypress";
import {findAllMatchingNodes} from "@angular/compiler-cli/src/ngtsc/typecheck/src/comments";

export default defineConfig({
  e2e: {
    baseUrl: "http://localhost:4200",
    viewportWidth: 1920,
    viewportHeight: 1080,
    supportFile: 'cypress/support/e2e.ts',
    watchForFileChanges: false,
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    retries: {
      runMode: 0,
      openMode: 0,
    },
    screenshotOnRunFailure: true,
  },

  component: {
    devServer: {
      framework: "angular",
      bundler: "webpack",
    },
    specPattern: "**/*.cy.ts",
  },
});
