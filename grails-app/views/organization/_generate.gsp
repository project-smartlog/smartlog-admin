<form id="new-certificates-form" name="new-certificates-form" class="container-fluid" action="${createLink(controller: "organization", action: "generate")}">

    <h3>Basic organization information</h3>
    <div class="form-group">
        <label>Name</label>
        <input type="text" class="form-control" id="new-organization-name" name="orgName" placeholder="Org2" value="${params.orgName}"/>
    </div>

    <div class="form-group">
        <label>Domain</label>
        <input type="text" class="form-control" id="new-organization-domain" name="orgDomain" placeholder="org2.example.com" value="${params.orgDomain}"/>
    </div>

    <div class="form-group">
        <label>MSP</label>
        <input type="text" class="form-control" id="new-organization-msp" name="orgMsp" placeholder="Org2MSP" value="${params.orgMsp}"/>
    </div>



    <h3>Peer config</h3>
    <div class="form-group">
        <label>Peer url</label>
        <input type="text" class="form-control" id="peer-url-input" name="peerUrl" placeholder="grpc://192.168.99.100:7051" value="${params.peerUrl}"/>
    </div>

    <div class="form-group">
        <label>Peer domain</label>
        <input type="text" class="form-control" id="peer-domain-input" name="peerDomain" placeholder="peer0.org1.example.com" value="${params.peerDomain}"/>
    </div>



    <h3>Orderer config</h3>
    <div class="form-group">
        <label>Orderer url</label>
        <input type="text" class="form-control" id="orderer-url-input" name="ordererUrl" placeholder="grpc://192.168.99.100:7050" value="${params.ordererUrl}"/>
    </div>

    <div class="form-group">
        <label>Orderer domain</label>
        <input type="text" class="form-control" id="orderer-domain-input" name="ordererDomain" placeholder="orderer.example.com" value="${params.ordererDomain}"/>
    </div>



    <h3>Eventhub config</h3>
    <div class="form-group">
        <label>Eventhub url</label>
        <input type="text" class="form-control" id="eventhub-url-input" name="eventhubUrl" placeholder="grpc://192.168.99.100:7053" value="${params.eventhubUrl}"/>
    </div>

    <div class="form-group">
        <label>Eventhub domain</label>
        <input type="text" class="form-control" id="eventhub-domain-input" name="eventhubDomain" placeholder="peer0.org1.example.com" value="${params.eventhubDomain}"/>
    </div>

    <h3>CouchDB url</h3>
    <div class="form-group">
        <label>CouchDB url</label>
        <input type="text" class="form-control" id="couchdburl-input" name="couchDbUrl" placeholder="http://192.168.99.100:5984" value="${params.couchDbUrl}"/>
    </div>

    <h3>Environment path and CA url</h3>
    <div class="form-group">
        <label>Client environment path</label>
        <input type="text" class="form-control" id="client-envpath-input" name="envpath" placeholder="C:\Users\samhon\fabric-samples\basic-network\" value="${params.envpath}"/>
    </div>

    <div class="form-group">
        <label>CA url</label>
        <input type="text" class="form-control" id="ca-url-input" name="caUrl" placeholder="http://192.168.99.100:7054" value="${params.caUrl}"/>
    </div>

    <div class="form-group">
        <label>Peer type</label>
        <select name="peerType" class="form-control" autocomplete="off">
            <g:peerTypeOptions selected="${params.peerType}"/>
        </select>
    </div>


    <div class="form-group">
        <button type="submit" class="btn btn-default">Generate</button>
    </div>
</form>