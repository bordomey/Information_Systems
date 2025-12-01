FROM payara/micro:5.2022.2-jdk11

COPY target/labwork-system.war /opt/payara/deployments/

EXPOSE 8080

CMD ["--deploymentDir", "/opt/payara/deployments", "--contextRoot", "/labwork-system", "--port", "8080"]