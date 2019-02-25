<form id="new-user-form" name="new-user-form" class="container-fluid" action="${createLink(controller: "apiUser", action: "addUser")}">

    <h3>Add API user</h3>
    <div class="form-group">
        <label>Name</label>
        <input type="text" class="form-control" id="new-user-name" name="name" placeholder="Corporation XYZ" value=""/>
    </div>

    <div class="form-group">
        <label>MSPID</label>
        <input type="text" class="form-control" id="new-user-mspid" name="mspid" placeholder="Org2" value=""/>
    </div>

    <div class="form-group">
        <label>Domain name</label>
        <input type="text" class="form-control" id="new-user-domain" name="domainName" placeholder="peer0.org1.example.com" value=""/>
    </div>

    <div class="form-group">
        <label>Basic authentication username</label>
        <input type="text" class="form-control" id="new-user-auth-username" name="username" placeholder="User123" value=""/>
    </div>

    <div class="form-group">
        <label>Basic authentication password</label>
        <input type="text" class="form-control" id="new-user-auth-password" name="password" placeholder="Secret!123" value=""/>
    </div>

    <div class="form-group">
        <label>Basic authentication token</label>
        <input type="text" class="form-control" id="new-user-authToken" name="authToken" placeholder="Basic VGVzdDpwYXNzd2Q=" value="" readonly/>
    </div>

    <div class="form-group">
        <button id="button-generate-authtoken" onclick="generateToken()" type="button" class="btn btn-default">Generate token</button>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-default">Save</button>
    </div>
</form>