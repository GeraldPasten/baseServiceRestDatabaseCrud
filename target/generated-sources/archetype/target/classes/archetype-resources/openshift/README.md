oc new-project prueba1
oc new-app postgresql POSTGRESQL_USER=user POSTGRESQL_PASSWORD=pass POSTGRESQL_DATABASE=testdb db=postgresql
oc new-build --binary=true --name=quarkus-prueba1 --image-stream=java:latest
oc start-build quarkus-prueba1 --from-dir=Quarkus-Camel\ --follow
oc apply -f /quarkus-camel/code-with-quarkus/openshift/service-route.yml
oc new-app quarkus-prueba1