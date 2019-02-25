<html>
    <head>
        <meta name="layout" content="main_layout"/>
        <asset:javascript src="main.js"/>
        <title>Generate</title>
    </head>
    <body>
        <h1 class="page-header">Login</h1>

        <div class="container">
            <form id="login-form" name="login-form" action="${createLink(controller: "auth", action: "login")}" method="POST">

                <div class="row">
                    <div class="form-group  col-lg-6">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                            <input id="auth-username-input" type="text" class="form-control" name="username" placeholder="Username" autofocus>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="form-group  col-lg-6">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                            <input id="auth-password-input" type="password" class="form-control" name="password" placeholder="Password">
                        </div>
                    </div>
                </div>

                <button class="btn  btn-success">Login</button>
            </form>
        </div>
    </body>
</html>
