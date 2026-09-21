"use strict";
(self.webpackChunkjupyterlab_open_url_parameter = self.webpackChunkjupyterlab_open_url_parameter || []).push([
  [568],
  {
    568: (module, exports, __webpack_require__) => {
      __webpack_require__.r(exports);
      __webpack_require__.d(exports, {
        default: () => plugin
      });

      var application = __webpack_require__(247);
      var apputils = __webpack_require__(162);
      var coreutils = __webpack_require__(261);
      var filebrowser = __webpack_require__(226);
      var translation = __webpack_require__(186);

      const routePattern = new RegExp("/(lab|notebooks|edit)/?");

      const plugin = {
        id: "jupyterlab-open-url-parameter:plugin",
        autoStart: true,
        requires: [application.IRouter, translation.ITranslator],
        optional: [filebrowser.IDefaultFileBrowser],
        activate: (app, router, translator, browser) => {
          const { commands } = app;
          const trans = (translator.load("jupyterlab")) || translation.nullTranslator;
          const commandId = "router:fromUrl";

          const clearFromUrl = requestPath => {
            const next = new URL(coreutils.URLExt.join(coreutils.PageConfig.getBaseUrl(), requestPath));
            next.searchParams.delete("fromURL");
            const { pathname, search } = next;
            router.navigate(`${pathname}${search}`, { skipRouting: true });
          };

          const openPath = path =>
            commands.execute("docmanager:open", {
              path,
              options: { ref: "_noref" }
            });

          const listRootEntries = async () => {
            try {
              const root = await app.serviceManager.contents.get("", { content: true });
              return Array.isArray(root == null ? void 0 : root.content) ? root.content : [];
            } catch (error) {
              return [];
            }
          };

          const findExistingPath = async url => {
            const filename = coreutils.PathExt.basename(url);
            const entries = await listRootEntries();
            const existingEntry = entries.find(entry => entry && entry.name === filename);
            if (existingEntry && existingEntry.path) {
              return existingEntry.path;
            }

            try {
              const model = await app.serviceManager.contents.get(filename, { content: false });
              return model && model.path ? model.path : filename;
            } catch (error) {
              try {
                const alternate = await app.serviceManager.contents.get("/" + filename, { content: false });
                return alternate && alternate.path ? alternate.path : filename;
              } catch (nestedError) {
                return null;
              }
            }
          };

          const uploadOrOpen = async url => {
            const existingPath = await findExistingPath(url);
            if (existingPath) {
              return openPath(existingPath);
            }

            let blob;
            let contentType = "";
            try {
              const response = await fetch(url);
              blob = await response.blob();
              contentType = response.headers.get("Content-Type") || "";
            } catch (error) {
              if (error.response && error.response.status !== 200) {
                error.message = trans.__("Could not open URL: %1", url);
              }
              return apputils.showErrorMessage(trans.__("Cannot fetch"), error);
            }

            try {
              const filename = coreutils.PathExt.basename(url);
              const file = new File([blob], filename, { type: contentType });
              const uploaded = await (browser == null ? void 0 : browser.model.upload(file));

              if (uploaded && uploaded.path) {
                return openPath(uploaded.path);
              }

              const fallbackPath = await findExistingPath(url);
              if (fallbackPath) {
                return openPath(fallbackPath);
              }
              return undefined;
            } catch (error) {
              return apputils.showErrorMessage(
                trans._p("showErrorMessage", "Upload Error"),
                error
              );
            }
          };

          commands.addCommand(commandId, {
            execute: async args => {
              const { request, search } = args;
              const routeMatch = request.match(routePattern) || [];
              if (!routeMatch) {
                return;
              }

              const params = new URLSearchParams(search);
              const urls = params.getAll("fromURL");
              if (!urls || urls.length === 0) {
                return;
              }

              const decodedUrls = urls.map(value => decodeURIComponent(value));
              const [matchedPath] = routeMatch;

              if ((matchedPath == null ? void 0 : matchedPath.includes("/notebooks")) ||
                  (matchedPath == null ? void 0 : matchedPath.includes("/edit"))) {
                const [firstUrl] = decodedUrls;
                await uploadOrOpen(firstUrl);
                clearFromUrl(request);
                return;
              }

              app.restored.then(async () => {
                await Promise.all(decodedUrls.map(url => uploadOrOpen(url)));
                clearFromUrl(request);
              });
            }
          });

          router.register({
            command: commandId,
            pattern: routePattern
          });
        }
      };
    }
  }
]);
