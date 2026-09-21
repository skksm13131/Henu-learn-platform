(function () {
  var FILE_PARAM = 'experimentFile';
  var TEMPLATE_PARAM = 'templateUrl';
  var TEMPLATE_VERSION_PARAM = 'templateVersion';
  var ITEM_PARAM = 'itemPk';
  var WORKSPACE_PARAM = 'workspace';
  var RESET_PARAM = 'reset';
  var CLEAN_PARAMS = [FILE_PARAM, TEMPLATE_PARAM, TEMPLATE_VERSION_PARAM, ITEM_PARAM, 'token'];
  var TRANSIENT_PARAMS = ['path', 'clone', 'reset'];

  function getLaunchConfig() {
    var params = new URLSearchParams(window.location.search);
    var file = params.get(FILE_PARAM);
    var templateUrl = params.get(TEMPLATE_PARAM);
    if (!file || !templateUrl) {
      return null;
    }
    var workspace = params.get(WORKSPACE_PARAM);
    var itemPk = params.get(ITEM_PARAM);
    if (!workspace && itemPk) {
      workspace = 'experiment-' + itemPk;
    }
    return {
      file: file,
      templateUrl: templateUrl,
      templateVersion: params.get(TEMPLATE_VERSION_PARAM),
      workspace: workspace,
      token: params.get('token') || getStoredAccessToken(),
      reset: params.get(RESET_PARAM) === '1' || params.get(RESET_PARAM) === 'true'
    };
  }

  function getStoredAccessToken() {
    try {
      return window.localStorage ? window.localStorage.getItem('token') : null;
    } catch (error) {
      return null;
    }
  }

  function clearLaunchParams(workspace) {
    var url = new URL(window.location.href);
    for (var i = 0; i < CLEAN_PARAMS.length; i++) {
      url.searchParams.delete(CLEAN_PARAMS[i]);
    }
    for (var j = 0; j < TRANSIENT_PARAMS.length; j++) {
      url.searchParams.delete(TRANSIENT_PARAMS[j]);
    }
    if (workspace) {
      url.searchParams.set(WORKSPACE_PARAM, workspace);
    }
    window.history.replaceState({}, document.title, url.toString());
  }

  function sleep(ms) {
    return new Promise(function (resolve) {
      window.setTimeout(resolve, ms);
    });
  }

  async function waitForApp() {
    for (var i = 0; i < 120; i++) {
      if (window.jupyterapp && window.jupyterapp.serviceManager && window.jupyterapp.commands) {
        await window.jupyterapp.restored;
        return window.jupyterapp;
      }
      await sleep(250);
    }
    throw new Error('JupyterLite app did not finish loading');
  }

  async function fileExists(contents, path) {
    try {
      await contents.get(path, { content: false });
      return true;
    } catch (error) {
      return false;
    }
  }

  async function fetchTemplateNotebook(templateUrl, token) {
    var headers = {};
    if (token) {
      headers.Authorization = 'Bearer ' + token;
    }
    var response = await window.fetch(templateUrl, {
      cache: 'no-store',
      headers: headers
    });
    if (!response.ok) {
      throw new Error('Failed to fetch template notebook: ' + response.status);
    }
    return response.json();
  }

  function getTemplateVersionKey(path) {
    return 'labcore:experiment-template-version:' + path;
  }

  function readStoredTemplateVersion(path) {
    try {
      return window.localStorage ? window.localStorage.getItem(getTemplateVersionKey(path)) : null;
    } catch (error) {
      return null;
    }
  }

  function writeStoredTemplateVersion(path, version) {
    if (!version) {
      return;
    }
    try {
      if (window.localStorage) {
        window.localStorage.setItem(getTemplateVersionKey(path), version);
      }
    } catch (error) {
      // Ignore storage failures; the notebook itself is still usable.
    }
  }

  async function ensureNotebook(contents, path, templateUrl, token, templateVersion, reset) {
    var exists = await fileExists(contents, path);
    var storedVersion = readStoredTemplateVersion(path);
    if (exists && !reset && (!templateVersion || storedVersion === templateVersion)) {
      return;
    }

    var notebook = await fetchTemplateNotebook(templateUrl, token);
    await contents.save(path, {
      type: 'notebook',
      format: 'json',
      content: notebook
    });
    writeStoredTemplateVersion(path, templateVersion);
  }

  function resolveFactoryName(app, path) {
    if (!app.docRegistry || !app.docRegistry.defaultWidgetFactory) {
      return null;
    }
    var factory = app.docRegistry.defaultWidgetFactory(path);
    return factory ? factory.name : null;
  }

  async function openNotebook(app, path) {
    var args = { path: path };
    var factory = resolveFactoryName(app, path);
    if (factory) {
      args.factory = factory;
    }
    await app.commands.execute('docmanager:open', args);
  }

  async function launchExperiment() {
    var config = getLaunchConfig();
    if (!config) {
      return;
    }

    var app = await waitForApp();
    var path = config.file;
    var contents = app.serviceManager.contents;

    await ensureNotebook(contents, path, config.templateUrl, config.token, config.templateVersion, config.reset);
    await openNotebook(app, path);
    clearLaunchParams(config.workspace);
  }

  launchExperiment().catch(function (error) {
    console.error('Failed to launch experiment notebook', error);
  });
})();
