<html>
    <head>
        <meta name="layout" content="main_layout"/>
        <asset:javascript src="main.js"/>
        <asset:javascript src="dashboard/dashboard.js"/>
        <title>Channel</title>
    </head>
    <body>
        <h1 class="page-header">Channel</h1>

        <div class="container-fluid">
            <div>
                <h2>Change channel application policy</h2>
            </div>
            <div class="">
                <span>One org signing:
                    <g:if test="${oneOrgSignEnabled}">
                        enabled, and it can't be disabled
                    </g:if>
                    <g:else>
                        disabled
                        <a href="${createLink(controller: "channel", action: "enableOneOrgSign")}">
                            <button class="btn btn-default">Enable</button>
                        </a>
                    </g:else>
                </span>
            </div>

            <h2>Channel configuration block</h2>
            <textarea class="form-control" rows="20">${config}</textarea>

            <h2>Admin org cryptography</h2>

            <g:if test="${adminOrgCryptoAdded}">
                <div>Admin org crypto already added</div>
            </g:if>
            <g:else>
                <div>Admin org crypto not added</div>
                <a href="${createLink(controller: "organization", action: "addAdminOrgCryptoToChannel")}">
                    <button class="btn btn-success">Add crypto</button>
                </a>
            </g:else>
        </div>
    </body>
</html>
