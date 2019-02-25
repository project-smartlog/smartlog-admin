<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">

    <g:each in="${organizations}" var="${organization}">
        <g:render template="/organization/organizationAccordion" model="[organization: organization]"/>
    </g:each>

</div>