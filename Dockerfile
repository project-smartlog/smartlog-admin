FROM openjdk:8-jre-slim

LABEL maintainer=""
LABEL com.centurylinklabs.watchtower.enable="true"

EXPOSE 8080

WORKDIR /opt/smartlog-admin

ARG CTXL_PORT=7059
ARG CTXL_BASEURL=http://localhost:${CTXL_PORT}
ARG SMARTLOG_CFGPATH=/etc/hyperledger/ext_config
ARG SMARTLOG_CFGFILE=config.json
ARG SMARTLOG_CAPATH=${SMARTLOG_CFGPATH}/ca
ARG SMARTLOG_CACRT=ca.crt
ARG SMARTLOG_CAKEY=ca.key
ARG SMARTLOG_CACRL=ca.crl
ARG SMARTLOG_CAPWD=ca.pwd
ARG SMARTLOG_SSLCFG=${SMARTLOG_CAPATH}/openssl.cnf
ARG WAR_NAME=SmartlogAdminClient-0.1.war
ARG FABRIC_ARCH=amd64
ARG FABRIC_VERSION=1.2.1
ARG SMARTLOG_MANAGEMENTDB_URL=
ARG SMARTLOG_MANAGEMENTDB_USERNAME=
ARG SMARTLOG_MANAGEMENTDB_PASSWORD=

ENV CTXL_BASEURL="${CTXL_BASEURL}" \
    SMARTLOG_CFGPATH="${SMARTLOG_CFGPATH}" \
    SMARTLOG_CFGFILE="${SMARTLOG_CFGFILE}" \
    SMARTLOG_CAPATH="${SMARTLOG_CAPATH}" \
    SMARTLOG_CACRT="${SMARTLOG_CACRT}" \
    SMARTLOG_CAKEY="${SMARTLOG_CAKEY}" \
    SMARTLOG_CACRL="${SMARTLOG_CACRL}" \
    SMARTLOG_CAPWD="${SMARTLOG_CAPWD}" \
    SMARTLOG_SSLCFG="${SMARTLOG_SSLCFG}" \
    WAR_NAME="${WAR_NAME}" \
    FABRIC_ARCH="${FABRIC_ARCH}" \
    FABRIC_VERSION="${FABRIC_VERSION}" \
    SMARTLOG_MANAGEMENTDB_URL="${SMARTLOG_MANAGEMENTDB_URL}" \
    SMARTLOG_MANAGEMENTDB_USERNAME="${SMARTLOG_MANAGEMENTDB_USERNAME}" \
    SMARTLOG_MANAGEMENTDB_PASSWORD="${SMARTLOG_MANAGEMENTDB_PASSWORD}"

ENTRYPOINT java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dconfigtxlator.baseurl="${CTXL_BASEURL}" -Dsmartlog.configpath="${SMARTLOG_CFGPATH}/${SMARTLOG_CFGFILE}" -Dsmartlog.cakey="${SMARTLOG_CAPATH}/${SMARTLOG_CAKEY}" -Dsmartlog.capwd="file:${SMARTLOG_CAPATH}/${SMARTLOG_CAPWD}" -Dsmartlog.cacert="${SMARTLOG_CAPATH}/${SMARTLOG_CACRT}" -Dsmartlog.crl="${SMARTLOG_CAPATH}/${SMARTLOG_CACRL}" -Dsmartlog.openssl.config="${SMARTLOG_SSLCFG}" -Dsmartlog.managementdb.url="${SMARTLOG_MANAGEMENTDB_URL}" -Dsmartlog.managementdb.username="${SMARTLOG_MANAGEMENTDB_USERNAME}" -Dsmartlog.managementdb.password="${SMARTLOG_MANAGEMENTDB_PASSWORD}" -jar ${WAR_NAME}

COPY templates ${SMARTLOG_CFGPATH}/templates/
COPY build/libs/${WAR_NAME} ${WAR_NAME}

RUN apt-get -qy update ; apt-get -qy install wget ; wget --no-check-certificate https://nexus.hyperledger.org/content/repositories/releases/org/hyperledger/fabric/hyperledger-fabric/linux-${FABRIC_ARCH}-${FABRIC_VERSION}/hyperledger-fabric-linux-${FABRIC_ARCH}-${FABRIC_VERSION}.tar.gz ; tar xvf hyperledger-fabric-linux-${FABRIC_ARCH}-${FABRIC_VERSION}.tar.gz bin/cryptogen bin/configtxgen ; cp bin/cryptogen bin/configtxgen /usr/local/bin/ ; rm hyperledger-fabric-linux-${FABRIC_ARCH}-${FABRIC_VERSION}.tar.gz ; rm -rf bin ; apt-get purge wget ; apt-get clean

