<html>
    <head>
        <meta name="layout" content="main_layout"/>
        <asset:javascript src="main.js"/>
        <asset:javascript src="apiUser/apiUser.js"/>
        <title>Edit ApiUsers</title>
    </head>
    <body>
        <h1 class="page-header">Add and remove API users</h1>

        <div class="container-fluid">
            <g:render template="/apiUser/getUsers" model="[apiUsers: apiUsers]"/>
        </div>

        <div class="container-fluid">
            <g:render template="/apiUser/addForm"/>
        </div>

    </body>
</html>
