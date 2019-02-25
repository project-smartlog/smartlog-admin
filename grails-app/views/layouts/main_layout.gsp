<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <!-- Bootstrap core CSS -->
    <asset:stylesheet src="bootstrap.css"/>
    <asset:stylesheet src="dashboard.css"/>

    <!-- Override CSS -->
    <asset:stylesheet src="override.css"/>

    <script src="//code.jquery.com/jquery-2.2.4.min.js" integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44=" crossorigin="anonymous"></script>
    <asset:javascript src="jquery-2.2.0.min.js"/>
    <g:layoutHead/>
</head>

<body>

<nav class="navbar navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${createLink(controller: "management", action: "index")}">SMARTLOG</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <g:if test="${session.loggedIn}">
                    <li><a href="${createLink(controller: "management", action: "index")}" <g:if test='${controllerName.toLowerCase().contains("management")}'>class="active"</g:if>><span class="glyphicon glyphicon-cog"></span> Management</a></li>
                    <li><a href="${createLink(controller: "auth", action: "logout")}" <g:if test='${controllerName.toLowerCase().contains("auth")}'>class="active"</g:if>><span class="glyphicon glyphicon-off"></span> Logout</a></li>
                </g:if>
                <g:else>
                    <li><a href="${createLink(controller: "auth", action: "login")}" <g:if test='${controllerName.toLowerCase().contains("auth")}'>class="active"</g:if>><span class="glyphicon glyphicon-user"></span> Login</a></li>
                </g:else>
            </ul>
        </div>
    </div>
</nav>


<g:if test="${session.loggedIn}">

    <div class="container-fluid">
        <div class="col-sm-3 col-md-2 sidebar">
            <ul class="nav nav-sidebar">
                <li <g:if test="${actionName.toLowerCase().contains("index")}">class="active"</g:if>><a href="${createLink(controller: "management", action: "index")}">Overview</a></li>
                <li <g:if test="${actionName.toLowerCase().contains("channel")}">class="active"</g:if>><a href="${createLink(controller: "management", action: "channel")}">Channel</a></li>
                <li <g:if test="${actionName.toLowerCase().contains("addorganization")}">class="active"</g:if>><a href="${createLink(controller: "management", action: "addOrganization")}">Add organization</a></li>
                <li <g:if test="${actionName.toLowerCase().contains("organizations")}">class="active"</g:if>><a href="${createLink(controller: "management", action: "organizations")}">Organizations</a></li>
                <li <g:if test="${actionName.toLowerCase().contains("transportchain")}">class="active"</g:if>><a href="${createLink(controller: "management", action: "transportChain")}">Transport chains</a></li>
                <li <g:if test="${actionName.toLowerCase().contains("apiusers")}">class="active"</g:if>><a href="${createLink(controller: "management", action: "apiUsers")}">API users</a></li>
            </ul>
        </div>

        <div class="row">
            <div class="container-fluid main">

                <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                    <g:layoutBody/>
                </div>
            </div>
        </div>
    </div>

</g:if>
<g:else>
    <div class="container">
        <g:layoutBody/>
    </div>
</g:else>

<div id="alert" class="alert alert-warning alert-dismissible fade in col-md-6 notification js-notification js-alert" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">×</span>
    </button>
    <p id="warning-message" class="js-warning-message">${flash.warning}</p>
</div>

<div id="error" class="alert alert-danger alert-dismissible fade in col-md-6 notification js-notification js-error" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">×</span>
    </button>
    <p id="error-message" class="js-error-message">${flash.error}</p>
</div>

<div id="success" class="alert alert-success alert-dismissible fade in col-md-6 notification js-notification js-success" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">×</span>
    </button>
    <p id="success-message" class="js-success-message">${flash.success}</p>
</div>

<div id="overlay" class="overlay  js-overlay"></div>

<!-- Bootstrap core JavaScript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<asset:javascript src="bootstrap.js"/>
</body>
</html>
