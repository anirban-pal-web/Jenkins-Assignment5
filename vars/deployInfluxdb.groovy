def call(Map params = [:]) {

    stage('Clone') {
        git params.REPO_URL
    }

    def cfg = readYaml(file: "${params.CODE_BASE_PATH}/config.yml")

    if (cfg.KEEP_APPROVAL_STAGE == true) {
        stage('User Approval') {
            input message: "Deploy to ${cfg.ENVIRONMENT}?", ok: "Approve"
        }
    }

    stage('Playbook Execution') {
        sh "ansible-playbook deploy-influxdb.yml"
    }

    stage('Notification') {
        slackSend channel: cfg.SLACK_CHANNEL_NAME,
                  message: cfg.ACTION_MESSAGE
    }
}
