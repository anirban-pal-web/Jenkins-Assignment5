def call(Map params) {

    def cfg = readYaml(text: libraryResource('prd_config.yml'))

    stage('Clone Repo') {
        echo "Cloning influxDB code..."
        dir('ansible-src') {
            git branch: 'Anirban',
                url: 'https://github.com/OT-MyGurukulam/Ansible_33.git',
                credentialsId: 'Git'
        }
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
