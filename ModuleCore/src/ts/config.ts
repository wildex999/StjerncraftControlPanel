(
    function () {
        requirejs.config({
            "baseUrl": "libs",
            "paths": {
                "jquery": "jquery.min"
            }
        });

        requirejs(["main"], function (Main) {
            let main = new Main();
            main.run();
        });
    })();