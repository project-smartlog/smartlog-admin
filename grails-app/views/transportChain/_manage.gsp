<div class="row">

    <div class="col-lg-8">

            <h3>Add new chain</h3>

            <form id="new-transport-chain-form" name="new-transport-chain-form" method="POST" action="${createLink(controller: 'transportChain', action: 'edit')}">

                <div class="form-group">
                    <label for="transport-chain-name-input">Name</label>
                    <input id="transport-chain-name-input" name="chainId" class="form-control"/>
                </div>

                <div class="form-group">
                    <label for="transport-chain-input">Chain</label>
                    <input id="transport-chain-input" name="chain" class="form-control"/>
                </div>

                <button type="submit" id="add-transport-chain-button" class="btn btn-success">Add</button>

            </form>

    </div>

    <div class="col-lg-4">
        <h3>Organizations</h3>

        <g:each in="${ orgs }" var="org">
            <div class="js-organization organization  btn btn-primary">${ org }</div>
        </g:each>
    </div>
</div>

<hr>

<div class="row">
    <div class="col-lg-8">
        <h3>Edit / remove chains</h3>

        <form id="edit-transport-chain-form" name="edit-transport-chain-form" method="POST">

            <div class="form-group">
                <label for="transport-chain-name-edit-input">Name</label>
                <input id="transport-chain-name-edit-input" name="chainId" class="form-control"/>
            </div>

            <div class="form-group">
                <label for="transport-chain-edit-input">Chain</label>
                <input id="transport-chain-edit-input" name="chain" class="form-control"/>
            </div>

            <button type="submit" class="btn btn-success" formaction="${createLink(controller: 'transportChain', action: 'edit')}">Edit</button>
            <button type="submit" formaction="${createLink(controller: 'transportChain', action: 'remove')}" class="btn btn-danger">Remove</button>
        </form>
    </div>

    <div class="col-lg-4">
        <h3>Chains</h3>

        <g:each in="${ chains }" var="chain">
            <div class="js-transport-chain transport-chain  btn btn-primary">
                <span class="transport-chain--id">${ chain.id }</span>
                <input class="transport-chain--chain" type="hidden" value="${ chain.participants.join(',')}">
            </div>
        </g:each>
    </div>
</div>

