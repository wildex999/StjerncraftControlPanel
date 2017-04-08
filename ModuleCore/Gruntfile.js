module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

		//Clean up anything from previous build, so we don't include old files
		clean: {
			build: ['build/']
		},
        //Copy the client libraries from node_modules to the libs folder
        copy: {
            prodLibs: {
                nonull: true,
                expand: true,
                flatten: true,
                cwd: 'node_modules',
                src: [
                        'jquery/dist/jquery.min.js',
                        'requirejs/require.js'
                    ],
                dest: 'build/libs', filter: 'isFile',
                filter: function(filepath) {
                    if(!grunt.file.exists(filepath)) {
                        grunt.fail.warn("Could not find: " + filepath);
                    } else {
                        return true;
                    }
                }
            },
            devLibs: {
                files: []
            },
			copyHtml: {
				files: [ {
					expand: true,
					cwd: 'src/',
					src: ['**/*.html'],
					dest: 'build/',
				} ]
			}
        },
		//Compile the javascript into single file
        exec: {
            compile: {
                command: 'tsc'
            },
            compileDebug: {
                command: 'tsc --inlineSourceMap true --inlineSources true'
            }
        }
    });

	grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-exec');

    // Default task(s).
    grunt.registerTask('copyLibs', ['copy:prodLibs']);
	grunt.registerTask('copyHtml', ['copy:copyHtml']);
    grunt.registerTask('build', ['clean', 'exec:compileDebug', 'copyLibs', 'copyHtml']);
    grunt.registerTask('buildRelease', ['clean', 'exec:compile', 'copyLibs', 'copyHtml']);

};