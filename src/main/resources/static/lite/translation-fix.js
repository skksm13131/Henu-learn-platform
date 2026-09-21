(function () {
  var loadingText = '\u52a0\u8f7d\u5b9e\u9a8c\u73af\u5883\u4e2d..';
  var overwriteTitle = '\u8986\u76d6\u6587\u4ef6\uff1f';
  var overwriteMessagePattern = /^"(.+)" already exists, overwrite\?$/;
  var overwriteMessageTemplate = '"$1" \u5df2\u5b58\u5728\uff0c\u662f\u5426\u8986\u76d6\uff1f';
  var cancelLabel = '\u53d6\u6d88';
  var overwriteLabel = '\u8986\u76d6';

  function setLoadingText() {
    var nodes = document.querySelectorAll('.jupyterlite-loading-indicator-text');
    for (var i = 0; i < nodes.length; i++) {
      var node = nodes[i];
      if (!node || !node.textContent) {
        continue;
      }
      if (node.textContent.indexOf('Loading JupyterLite') !== -1) {
        node.textContent = loadingText;
      }
    }
  }

  function translateOverwriteDialog(dialog) {
    if (!dialog) {
      return;
    }

    var header = dialog.querySelector('.jp-Dialog-header');
    var body = dialog.querySelector('.jp-Dialog-body');
    var ariaLabel = dialog.getAttribute('aria-label') || '';
    var headerText = header && header.textContent ? header.textContent : '';
    var bodyTextRaw = body && body.textContent ? body.textContent : '';
    var combinedText = headerText + bodyTextRaw + ariaLabel;

    if (combinedText.indexOf('Overwrite file?') === -1 &&
        combinedText.indexOf('already exists, overwrite?') === -1) {
      return;
    }

    if (header && header.textContent && header.textContent.indexOf('Overwrite file?') !== -1) {
      header.textContent = header.textContent.replace('Overwrite file?', overwriteTitle);
    }

    if (body && body.textContent) {
      var bodyText = body.textContent.trim();
      var bodyMatch = bodyText.match(overwriteMessagePattern);
      if (bodyMatch) {
        body.textContent = overwriteMessageTemplate.replace('$1', bodyMatch[1]);
      }
    }

    if (ariaLabel) {
      var newAria = ariaLabel;
      if (newAria.indexOf('Overwrite file?') !== -1) {
        newAria = newAria.replace('Overwrite file?', overwriteTitle);
      }
      var ariaMatch = newAria.match(overwriteMessagePattern);
      if (ariaMatch) {
        newAria = newAria.replace(overwriteMessagePattern, overwriteMessageTemplate.replace('$1', ariaMatch[1]));
      }
      if (newAria !== ariaLabel) {
        dialog.setAttribute('aria-label', newAria);
      }
    }

    var walker = document.createTreeWalker(dialog, NodeFilter.SHOW_TEXT);
    var node = walker.nextNode();
    while (node) {
      var value = node.nodeValue;
      if (value) {
        var trimmed = value.trim();
        if (trimmed === 'Overwrite file?') {
          node.nodeValue = value.replace(trimmed, overwriteTitle);
        } else {
          var match = trimmed.match(overwriteMessagePattern);
          if (match) {
            node.nodeValue = value.replace(trimmed, overwriteMessageTemplate.replace('$1', match[1]));
          }
        }
      }
      node = walker.nextNode();
    }

    var labelNodes = dialog.querySelectorAll('.jp-Dialog-buttonLabel');
    for (var j = 0; j < labelNodes.length; j++) {
      var labelNode = labelNodes[j];
      if (!labelNode || !labelNode.textContent) {
        continue;
      }
      var labelText = labelNode.textContent.trim();
      if (labelText === 'Cancel') {
        labelNode.textContent = cancelLabel;
        labelNode.setAttribute('aria-label', cancelLabel);
      } else if (labelText === 'Overwrite') {
        labelNode.textContent = overwriteLabel;
        labelNode.setAttribute('aria-label', overwriteLabel);
      }
    }

    var buttons = dialog.querySelectorAll('button');
    for (var k = 0; k < buttons.length; k++) {
      var btn = buttons[k];
      if (!btn || !btn.textContent) {
        continue;
      }
      var label = btn.textContent.trim();
      if (label === 'Cancel') {
        btn.textContent = cancelLabel;
        btn.setAttribute('aria-label', cancelLabel);
      } else if (label === 'Overwrite') {
        btn.textContent = overwriteLabel;
        btn.setAttribute('aria-label', overwriteLabel);
      }
    }
  }

  function scanDialogs(root) {
    if (!root || !root.querySelectorAll) {
      return;
    }
    if (root.matches && (root.matches('.jp-Dialog') || root.matches('.jp-Dialog-content') || root.matches('[role="dialog"]'))) {
      translateOverwriteDialog(root);
    }
    var dialogs = root.querySelectorAll('.jp-Dialog, .jp-Dialog-content, [role="dialog"]');
    for (var i = 0; i < dialogs.length; i++) {
      translateOverwriteDialog(dialogs[i]);
    }
  }

  function findDialog(node) {
    if (!node) {
      return null;
    }
    var element = node.nodeType === 3 ? node.parentElement : node;
    if (!element || !element.closest) {
      return null;
    }
    return element.closest('.jp-Dialog, .jp-Dialog-content, [role="dialog"]');
  }

  function handleMutations(mutations) {
    for (var i = 0; i < mutations.length; i++) {
      var mutation = mutations[i];
      if (mutation.type === 'characterData') {
        var dialog = findDialog(mutation.target);
        if (dialog) {
          translateOverwriteDialog(dialog);
        }
        continue;
      }
      if (mutation.type === 'attributes') {
        var attrDialog = findDialog(mutation.target);
        if (attrDialog) {
          translateOverwriteDialog(attrDialog);
        }
        continue;
      }
      if (!mutation.addedNodes || mutation.addedNodes.length === 0) {
        continue;
      }
      for (var j = 0; j < mutation.addedNodes.length; j++) {
        var node = mutation.addedNodes[j];
        if (!node || node.nodeType !== 1) {
          continue;
        }
        scanDialogs(node);
        var nearestDialog = findDialog(node);
        if (nearestDialog) {
          translateOverwriteDialog(nearestDialog);
        }
      }
    }
  }

  function init() {
    setLoadingText();
    scanDialogs(document.body || document.documentElement);

    var observer = new MutationObserver(handleMutations);
    observer.observe(document.documentElement || document.body, {
      childList: true,
      subtree: true,
      characterData: true,
      attributes: true,
      attributeFilter: ['aria-label']
    });

    var retries = 0;
    var timer = setInterval(function () {
      retries += 1;
      setLoadingText();
      scanDialogs(document.body || document.documentElement);
      if (retries >= 20) {
        clearInterval(timer);
      }
    }, 500);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
