#!groovy

pipeline {
	agent {
		docker { image 'openjdk:8-jdk' }
	}

	options {
		timeout(time: 2, unit: 'HOURS')
		timestamps()
		buildDiscarder(logRotator(numToKeepStr: '30'))
	}

	stages {

		stage('Unit and Device tests 2') {
			steps {
			    sh "cp 2/report.xml ."
			}

			post {
				always {
					publishCoverage adapters: [jacocoAdapter("report2.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD'), tag: "foo"
				}
			}
		}
		stage('Unit and Device tests') {
			steps {
			    sh "cp 1/report.xml ."
			}

			post {
				always {
					publishCoverage adapters: [jacocoAdapter("report1.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD'), tag: "foo"
				}
			}
		}
	}

}
