pipelineJob('madcore.plugin.offlineimap.deploy') {
    parameters {
      stringParam('REPO_URL', 'https://github.com/madcore-ai/containers', '')
      stringParam('REPO_BRANCH', 'docker', '')
      stringParam('APP_NAME', 'offlineimap', '')
      stringParam('DOCKERFILE_PATH', 'offlineimap', 'Specify path to docker file relative to root repo.')
      stringParam('APP_NAMESPACE', 'offlineimap-plugin', 'Plugin namespase')

      stringParam('REMOTE_USER', 'coxodox@gmail.com', '')
      stringParam('OAUTH2_CLIENT_ID', '846427981560-l7a2oeb1m29moj183l9g0ruq0vlbbuic.apps.googleusercontent.com', '')
      stringParam('OAUTH2_CLIENT_SECRET', 'YjYdktcPwa-RBomNZvkuuTMx', '')
      stringParam('OAUTH2_REFRESH_TOKEN', '1/yR5OP7m9DnSY6oN7wvVhOYXQNSp9k9cYkjupdcyWLsjkWA7wEA-rykT_11sJkyyX', '')




    }

    definition {
	    cps {
            sandbox()
            script("""
            node {
                stage ('Docker: build container') {
                  build job: 'madcore.docker.image.build', parameters: [string(name: 'REPO_URL', value: params.REPO_URL), string(name: 'APP_NAME', value: params.APP_NAME), string(name: 'DOCKERFILE_PATH', value: params.DOCKERFILE_PATH)]
                }
                stage ('Docker: registry publish') {
                  build job: 'madcore.docker.registry.publish', parameters: [string(name: 'APP_NAME', value: params.APP_NAME)]
                }
                stage ('Docker: registry status') {
                  build job: 'madcore.docker.registry.status', parameters: [string(name: 'APP_NAME', value: params.APP_NAME)]
                }
                stage ('Generate rc yaml') {
                  build job: 'madcore.plugin.offlineimap.render.template', parameters: [
                    string(name: 'APP_NAME', value: params.APP_NAME),
                    string(name: 'REMOTE_USER', value: params.REMOTE_USER),
                    string(name: 'OAUTH2_CLIENT_ID', value: params.OAUTH2_CLIENT_ID),
                    string(name: 'OAUTH2_CLIENT_SECRET', value: params.OAUTH2_CLIENT_SECRET),
                    string(name: 'OAUTH2_REFRESH_TOKEN', value: params.OAUTH2_REFRESH_TOKEN)


                    ]
                }
                stage ('Kubernetes: create') {
                  build job: 'madcore.kubectl.create', parameters: [string(name: 'FILENAME', value: 'offlineimap/kub')]
                }
              }
            """.stripIndent())
	    }
    }
}
