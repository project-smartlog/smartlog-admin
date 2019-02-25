<div class="panel panel-default">
    <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapse${organization.uuid.toString()}" aria-expanded="true" aria-controls="collapse${organization.uuid.toString()}">
        <div class="panel-heading" role="tab" id="heading${organization.uuid.toString()}">
            <h4 class="panel-title"> ${organization.domain}</h4>
        </div>
    </a>
    <div id="collapse${organization.uuid.toString()}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading${organization.uuid.toString()}">
        <div class="panel-body">

            <div class="container-fluid">
                <div class="org-config-container col-xs-12 col-md-6">
                    <h3>Client config</h3>
                    <textarea class="text-area  org-config-text-area" rows="23">${organization.config}</textarea>
                </div>

                <div class="org-detail-container col-xs-12 col-md-6">

                    <h3>Configs</h3>

                    <a href="${createLink(controller: "organization", action: "downloadConfigs", params: [orgDomain: organization.domain])}">
                        <button class="btn btn-primary">Download configs</button>
                    </a>

                    <h3>Organization configured to channel: ${organization.inChannel}</h3>

                </div>
            </div>

        </div>
    </div>
</div>