my_node = k8sagent(name: 'maven3.8.5.jdk17.autotest')
podTemplate(my_node) {
  node(my_node.label) {
    env.PROJECT_NAME = "stp-new-de"
    env.SERVICE_NAME = "autotest-service"
    maven_common_be_jdk17_newde_autotest()
  }
}