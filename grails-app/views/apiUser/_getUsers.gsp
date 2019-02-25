<h3>Existing API users</h3>
<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">

    <g:each in="${apiUsers}" var="${apiUser}">
        <g:render template="/apiUser/userAccordion" model="[apiUser: apiUser]"/>
    </g:each>

</div>