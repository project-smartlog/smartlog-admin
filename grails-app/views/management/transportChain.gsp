<html>
    <head>
        <meta name="layout" content="main_layout"/>
        <asset:javascript src="main.js"/>
        <asset:javascript src="dashboard/dashboard.js"/>
        <asset:javascript src="transportChain/transportChain.js"/>
        <title>Transport chains</title>
    </head>
    <body>
        <h1 class="page-header">Transport chains</h1>

        <div class="container-fluid">
            <g:render template="/transportChain/manage" model="[chains: chains, orgs: orgs]"/>
        </div>
    </body>
</html>
