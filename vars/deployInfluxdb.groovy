def call(Map params) {

    def cfg = readYaml(text: libraryResource('prd_config.yml'))

    stage('Clone') {
        git params.REPO_URL
    }

    if (cfg.KEEP_APPROVAL_STAGE == true) {
        stage('User Approval') {
            input message: "Deploy to ${cfg.ENVIRONMENT}?", ok: "Approve"
        }
    }

    stage('Playbook Execution') {
        sh "ansible-playbook ${cfg.PLAYBOOK}"
    }

    stage('Notification') {
        slackSend channel: cfg.SLACK_CHANNEL_NAME,
                  message: cfg.ACTION_MESSAGE
    }
}
