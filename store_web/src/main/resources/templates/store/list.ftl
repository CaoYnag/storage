<html>
<head>
    <title>Storage</title>
    <link href="${ctx.contextPath}/static/css/store.css" type="text/css" rel="stylesheet"/>
    <script type="text/javascript" src="${ctx.contextPath}/static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="${ctx.contextPath}/static/nbui/uikit-1.1.6/boot.js"></script>
    <script type="text/javascript" src="${ctx.contextPath}/static/js/sweetalert2.min.js"></script>
    <link href="${ctx.contextPath}/static/css/sa2/dark.css" type="text/css" rel="stylesheet"/>
    <script type="text/javascript" src="${ctx.contextPath}/static/js/common.js"></script>
</head>
<body>
    <#if stores??>
    <div class="stores">
        <table id="contentTable"
               class="uk-table uk-table-hover uk-table-striped uk-table-border">
            <thead class="table-oper">
            <th colspan="5">
                <div class="left">
                    <i id="modify" class="uk-icon-gear"></i>
                </div>
                <div class='right'>
                    <i class="add uk-icon-plus-square store add"></i>
                </div>
            </th>
            </thead>
            <thead>
            <tr>
                <th style="width: 4%">Name</th>
                <th style="width: 4%">Driver</th>
                <th style="width: 2%">Perm</th>
                <th style="width: 6%">操作</th>
            </tr>
            </thead>
            <tbody>
        <#list stores as store>
        <tr>
            <td id="${store.name}_nm">${store.name}</td>
            <td id="${store.name}_dv">${store.driver}</td>
            <td id="${store.name}_pm">${store.perm}</td>
            <td id="${store.name}_cf" class="hide">${store.conf}</td>
            <td>
                <a id="detail_${store.name}" class="store detail"><i class="uk-icon-search"></i>详情</a>
                <a id="delete_${store.name}" class="store delete"><i class="uk-icon-trash"></i>删除</a>
            </td>
        </tr>
        </#list>
            </tbody>
        </table>
    </div>
    <#else>
        <legend>Empty</legend>
    </#if>
<div class="uk-modal" id="updateModal">
    <div class="uk-modal-dialog">
        <div class="uk-modal-header">
            <h4 class="tm-article-subtitle">添加Store</h4>
        </div>

        <div>
            <table class="uk-form uk-form-horizontal">
                <tr>
                    <td>Name</td>
                    <td><input type="text" id="uName"></td>
                </tr>
                <tr>
                    <td>Driver</td>
                    <td><input type="text" list="driver_list" id="uDriver">
                        <datalist id="driver_list">
                        <#list drivers as driver>
                        <td id="d_${driver.first}">${driver.second}</td>
                        </#list>
                        </datalist></td>
                </tr>
                <tr>
                    <td>Perm</td>
                    <td><input type="text" id="uPerm"></td>
                </tr>
                <tr>
                    <td>Conf</td>
                    <td><input type="text" id="uConf"></td>
                </tr>
            </table>
        </div>

        <div class="uk-modal-footer uk-text-right">
            <button type="button" class="uk-button uk-button-primary" id='btn_update' onclick="submitForm()">保存</button>
            <button type="button" class="uk-button uk-modal-close">取消</button>
        </div>
    </div>
</div>
<div class="uk-modal" id="infoModal">
    <div class="uk-modal-dialog">
        <div class="uk-modal-header">
            <h2 class="tm-article-subtitle" id="storeName"></h2>
        </div>
        <hr class="divider-simple uk-article-divider">
        <div>
            <table class="uk-form uk-form-horizontal store info">
                <tr>
                    <td>Name</td>
                    <td><div class="store info value uk-text-break" id="iName"></div></td>
                </tr>
                <tr>
                    <td>Driver</td>
                    <td><div class="store info value uk-text-break" id="iDriver"></div></td>
                </tr>
                <tr>
                    <td>Perm</td>
                    <td><div class="store info value uk-text-break" id="iPerm"></div></td>
                </tr>
                <tr>
                    <td>Conf</td>
                    <td><div class="store info value uk-text-break" id="iConf"></div></td>
                </tr>
            </table>
            <hr class="divider-simple uk-article-divider">
            <table id="typesTb" class="uk-table uk-table-hover uk-table-striped uk-table-border">
                <th>数据类型</th>
                <th>支持</th>
            <#if types??>
                <#list types as type>
                <tr>
                    <td id="${type}" class="store type">${type}</td>
                    <td id="${type}_sp" class="store support"><img src="${ctx.contextPath}/static/img/loading.gif"></td>
                </tr>
                </#list>
            <#else>
        <tr>
            <td colspan="2">Empty</td>
        </tr>
            </#if>
            </table>
        </div>

        <div class="uk-modal-footer uk-text-right">
            <button type="button" class="uk-button uk-button-primary" id='btn_delete' onclick="submitDelete()">删除
            </button>
            <button type="button" class="uk-button uk-modal-close">取消</button>
        </div>
    </div>
</div>
<script>
    function fillForm(name, driver, perm, conf) {
        $("#storeName").html(name);
        $("#iName").html(name);
        $("#iDriver").html(driver);
        $("#iPerm").html(perm);
        $("#iConf").html(conf);
    }

    function submitForm() {
        $.post("${ctx.contextPath}/api/store/stores", {
            name: $("#uName").val(),
            driver: $("#uDriver").val(),
            perm: $("#uPerm").val(),
            conf: $("#uConf").val()
        }, function (data) {
            console.log(data);
            if (data.status === "error") {
                Swal.fire(
                        '错误',
                        data.msg,
                        'error'
                );
                return;
            }
            window.location.reload();
        });
    }

    function submitDelete() {
        var nm = $("#uName").val();
        if (nm == null || nm === "") {
            error_msg("未指定数据");
            return;
        }
        deleteStore(nm);
    }

    function deleteStore(nm) {
        Swal.fire({
            title: '确认',
            text: '确定删除吗？',
            type: 'question',
            showCancelButton: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.value) {
                $.ajax({
                    url: "${ctx.contextPath}/api/store/stores/" + nm,
                    method: "delete",
                    success: function (data) {
                        if (data.status === "error") {
                            Swal.fire(
                                    '错误',
                                    data.msg,
                                    'error'
                            );
                            return;
                        }
                        window.location.reload();
                    },
                    error: function (data) {
                        Swal.fire(
                                '错误',
                                data.msg,
                                'error'
                        )
                    }
                });
            }
        });
    }

    $(document).ready(function () {
        // imple with spring model instead of ajax
        <#--$.get("${ctx.contextPath}/api/store/drivers/", function(data){-->
        <#--    console.log(data);-->
        <#--    let html = "";-->
        <#--    for(let driver of data)-->
        <#--        html += "<option value='d_" + driver.replace(/\./g, "_") + "'>" + driver + "</option>";-->
        <#--    $('#driver_list').html(html);-->
        <#--});-->
        $(".store.detail").click(function () {
            let nm = $(this).attr("id").replace("detail_", "");
            console.log(nm);
            fillForm(nm, $("#" + nm + "_dv").html(), $("#" + nm + "_pm").html(), $("#" + nm + "_cf").html());
            UIkit.modal("#infoModal").show();

            let types = new Array();
            $(".store.type").each(function(){types.add($(this).attr("id"));});
            console.log(types);
            if(types.length > 0){
                let url = "${ctx.contextPath}/api/store/types/";
                for(let tp of types)
                    url += tp + ",";
                url += "%";
                url = url.replace(",%", "").replace("%", "");
                url += "/" + nm;
                $.get(url, function(data){
                    console.log(data);
                    for(let tp of types){
                        if(data.data[tp] == null) // unknown
                            $("#" + tp + "_sp").html("<div class=\"uk-badge uk-badge-warning\">unknown</div>");
                        else if(data.data[tp] == "true") // succ
                            $("#" + tp + "_sp").html("<div class=\"uk-badge uk-badge-success\">succ</div>");
                        else if(data.data[tp] === "false") // fail
                            $("#" + tp + "_sp").html("<div class=\"uk-badge uk-badge-danger\">fail</div>");
                        else if(data.data[tp] === "miss") // miss
                            $("#" + tp + "_sp").html("<div class=\"uk-badge uk-badge-warning\">miss</div>");
                    }
                });
            }
        });
        $(".store.delete").click(
                function () {
                    let nm = $(this).attr("id").replace("delete_", "");
                    deleteStore(nm);
                });
        $(".store.add").click(function () {
            UIkit.modal("#updateModal", {bgclose: false}).show();
        });
    });
</script>
</body>
</html>
