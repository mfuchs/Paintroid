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
		stage('Unit and Device tests') {
			steps {
			    echo "hello world"
			}

			post {
				always {
					publishCoverage adapters: [jacocoAdapter("1/report.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD'), tag: "foo"
					publishCoverage adapters: [jacocoAdapter("2/report.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD'), tag: "foo"

				}
			}
		}
	}

}
