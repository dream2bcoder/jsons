
(function (window) {

    if (!window.File || !window.FileList || !window.FileReader) {
        console.log("Your browser does not support File API");
        return;
    }

    var CodeParser = function () {
        this.onFileUploadHandler = this.onFileUploadHandler.bind(this);
        this.onFileLoadHandler = this.onFileLoadHandler.bind(this);
    };
    window.CodeParser = CodeParser;
    CodeParser.prototype.initialize = function (filesInput, cb) {
        if (filesInput.tagName.toUpperCase() !== 'INPUT' || filesInput.type !== 'file')
            throw new Error('Expected input file type :(');
        this.filesInput = filesInput;
        this.onParsed = cb;
        this.filesInput.addEventListener("change", this.onFileUploadHandler);
    };
    CodeParser.prototype.onFileUploadHandler = function (event) {
        var files = event.target.files; //FileList object

        for (var i = 0; i < files.length; i++) {
            var file = files[i];

            // Only plain text
            // if (!file.type.match('plain')) continue;

            var textReader = new FileReader();
            textReader.addEventListener("load", this.onFileLoadHandler);
            //Read the text file
            textReader.readAsText(file);
        }

        event.target.value = "";
    };
    CodeParser.prototype.onFileLoadHandler = function (event) {
        var o = CodeParser.parseText(event.target.result);
        if (o) {
            this.onParsed(o);
        }
    };
    CodeParser.parseText = function (rawText) {
        var o = {};
        rawText.replace(/\@\@(.*)\{((.*)+|(.*|[\r\n]+|.*)+)\}\@\@/igm, function (matched, group1, group2) {
            o[group1] = (o[group1] || '') + group2.replace(/.*[\r\n]+/, '').replace(/[\r\n]+.*$/, '').replace(/\r\n/gm, '<br>').replace(/\s/gm, '&nbsp;');
        });
        return o;
    };
    CodeParser.parseHtml = function (htmlText) {
        var parser = new DOMParser;
        var dom = parser.parseFromString(htmlText, 'text/html');
        if (dom.getElementsByTagName("parsererror").length > 0) {
            var parsererrorNS = dom.getElementsByTagName("parsererror")[0].namespaceURI;
            if (dom.getElementsByTagNameNS(parsererrorNS, 'parsererror').length > 0) {
                console.log('Error parsing XML');
                dom.body.innerHTML = "";
            }
        }
        return dom.body;
    };
    CodeParser.getText = function (htmlText) {
        var e = CodeParser.parseHtml(htmlText);
        return e.innerText;
    };

})(window);
