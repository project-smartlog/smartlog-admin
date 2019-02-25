<html>
    <head>
        <meta name="layout" content="main_layout"/>
        <asset:javascript src="main.js"/>
        <asset:javascript src="dashboard/dashboard.js"/>
        <title>Organizations</title>
    </head>
    <body>
        <h1 class="page-header">Configured organizations</h1>

        <div class="container-fluid">
            <g:render template="/organization/listOrganizations" model="[organizations: organizations]"/>
        </div>
    </body>
</html>
