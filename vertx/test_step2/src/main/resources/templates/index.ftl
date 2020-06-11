<#include "header.ftl">

<div class="row">

    <div class="col-md-12 mt-1">
        <div class="float-right">
            <h3>Create new page</h3>
            <form class="form-inline" action="/create" method="post">
                <div class="form-group">
                    <label style="margin-right:20px">Page:</label>
                    <input type="text" class="form-control" id="name" name="name" placeholder="New page name">
                </div>
            </form>
            <button type="submit" class="btn btn-primary" style="float:right; margin-top:20px">Create</button>
        </div>
        <h1 class="display-4">${title}</h1>
    </div>

    <div class="col-md-12 mt-1">
        <#list pages>
        <h2>Pages:</h2>
        <ul>
            <#items as page>
            <li><a href="/wiki/${page}">${page}</a></li>
        </#items>
        </ul>
        <#else>
        <p>The wiki is currently empty!</p>
    </#list>
</div>

</div>

<#include "footer.ftl">
