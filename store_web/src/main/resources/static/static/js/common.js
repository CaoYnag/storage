function info_notify(msg){
    UIkit.notify({
        message: "<i class='uk-icon-bell uk-icon-medium'></i>" + msg,
        status: 'info',
        timeout: 1000,
        pos: 'top-right'
    });
}

function succ_notify(msg){
    UIkit.notify({
        message: "<i class='uk-icon-check uk-icon-medium'></i>" + msg,
        status: 'success',
        timeout: 1000,
        pos: 'top-right'
    });
}

function warn_notify(msg){
    UIkit.notify({
        message: "<i class='uk-icon-exclamation uk-icon-medium'></i>" + msg,
        status: 'warning',
        timeout: 3000,
        pos: 'top-right'
    });
}

function err_notify(msg){
    UIkit.notify({
        message: "<i class='uk-icon-exclamation-triangle uk-icon-medium'></i>" + msg,
        status: 'danger',
        timeout: 0,
        pos: 'top-right'
    });
}

function SyncGet(url, succ, err){
    $.ajax({
        async: false,
        url: url,
        error: err,
        success:succ
    });
}

function Delete(url, fn){
    $.ajax({
        url: url,
        method: "delete",
        success: function(data){
            data = eval("(" + data + ")");
            if(data.status == "error"){
                err_notify(data.msg);
                return;
            }
            fn(data);
        },
        error: function(data){
            console.log(data);
            err_notify("Failed.");
        }
    });
}

function Get(url, fn){
    $.get(url, function(data){
        data = eval("(" + data + ")");
        if(data.status == "error"){
            err_notify(data.msg);
            return;
        }
        fn(data);
    });
}

function Post(url, data, fn){
    $.post(url, data, function(data){
        data = eval("(" + data + ")");
        if(data.status == "error"){
            err_notify(data.msg);
            return;
        }
        fn(data);
    });
}

function addUserRecur(arr, node) {
    if (node.id != null) {
        SyncGet("/user/manage/group/" + node.id + "/users", function(data){
            data = eval("(" + data + ")");
            console.log(data);
            if(data.status == "ok"){
                arr = arr.concat(data.data);
            }
        });
    }

    if (node.children != null) {
        for (var sub of node.children) {
            addUserRecur(arr, sub);
        }
    }
}


function findRoleRecurHelper(set, node){
    if(node == null) return;
    var roles = findRoleOfGroup(node);
    for(var role of roles)
        set.add(role);
    findRoleRecurHelper(set, node.getParentNode());
}

function findRoleRecur(node){
    var set = new Set();
    findRoleRecurHelper(set, node);
    return set;
}


function getFilterStr(filter){
    var str = "";
    if(filter.perms != null && filter.perms.length > 0){
        str += "perm[";
        for(var perm of filter.perms){
            str += perm.cname + ","
        }
        str += "]";
        str = str.replace(/,]/g, "],");
    }
    if(filter.constraints != null)
        str += filter.constraints;
    if(str == "" || str.length <= 0)
        str = "anon";

    return str;
}

function exists(item, list){
    for(var i of list){
        if(i.id == item.id)
            return true;
    }
    return false;
}
