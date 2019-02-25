<div class="panel panel-default">
    <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapse${apiUser.getId()}" aria-expanded="true" aria-controls="collapse${apiUser.getId()}">
        <div class="panel-heading" role="tab" id="heading${apiUser.getId()}">
            <h4 class="panel-title"> ${apiUser.name}</h4>
        </div>
    </a>
    <div id="collapse${apiUser.getId()}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading${apiUser.getId()}">
        <div class="panel-body">

            <div class="container-fluid">
                <div class="org-config-container col-xs-12 col-md-6">
                    <h3>Organisation information</h3>

                    <p>Name: ${apiUser.name}</p>
                    <p>MSPID: ${apiUser.organisation}</p>
                    <p>Domain name: ${apiUser.domainName}</p>

                    <h3>Authentication information</h3>

                    <p>Username: ${apiUser.username}</p>
                    <p>Password: ${apiUser.password}</p>
                    <p>Authentication token: ${apiUser.authToken}</p>

                    <a href="${createLink(controller: "apiUser", action: "deleteUser", params: [id: apiUser.getId()])}">
                        <button class="btn btn-primary">Delete</button>
                    </a>

                </div>
            </div>

        </div>
    </div>
</div>