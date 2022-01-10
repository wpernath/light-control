var selectedRoom = [];

$(function(){
    var hash = location.hash;
    var target = hash.length > 0 ? hash.substr(1) : "intro";
    
    var referencedRoomId = 0;
    if( target.indexOf("?id=") != -1 ) {
        referencedRoomId = target.substring(target.indexOf("=")+1);
        target = target.substring(0, target.indexOf("?"));
        console.log("Referenced Room ID = " + referencedRoomId);
        console.log( "target: " + target);
    } 

    var link = $(".navview-menu a[href*="+target+"]");
    var menu = link.closest("ul[data-role=dropdown]");
    var node = link.parent("li").addClass("active");

    console.log( "hash  : " + hash);
    console.log( "target: " + target);

    function getContent(target){
        window.on_page_functions = [];
        $.get(target + ".html").then(
            function(response){
                $("#content-wrapper").html(response);

                window.on_page_functions.forEach(function(func){
                    Metro.utils.exec(func, []);
                });
            }
        );
    }

    getContent(target);

    if (menu.length > 0) {
        Metro.getPlugin(menu, "dropdown").open();
    }

    $(".navview-menu").on(Metro.events.click, "a", function(e){
        var href = $(this).attr("href");
        var pane = $(this).closest(".navview-pane");
        var hash;

        console.log(" in navview-menu...");
        if (Metro.utils.isValue(href) && href.indexOf(".html") > -1) {
            document.location.href = href;
            return false;
        }

        if (href === "#") {
            return false;
        }

        hash = href.substr(1);

        var referencedRoomId = "";
        if( hash.indexOf("?id=") != -1 ) {
            referencedRoomId = hash.substring(hash.indexOf("=")+1);
            hash = hash.substring(0, hash.indexOf("?"));
            console.log("Referenced Room ID = " + referencedRoomId);
            console.log( "hash: " + hash);     
            
            //var app = angular.module("HueManagement");
            //app.name;


            
            $(this).json("/api/rooms/q?rn="+referencedRoomId).then(
                function(response) {
                    console.log(response.data);
                    selectedRoom = response.data;
                },
                function(xhr) {
                    console.log(xhr.status, xhr.statusText);
                }
            );


        } 

        href = hash + ".html";


        getContent(hash);

        if (pane.hasClass("open")) {
            pane.removeClass("open");
        }

        pane.find("li").removeClass("active");
        $(this).closest("li").addClass("active");

        window.history.pushState(href, href, "index.html#"+hash);

        return false;
    });

    function updateOrderStatus(){
        var val = $("#sel-statuses").val();
        var table = $("#table-order-statuses").find("tbody");
        var tr, td;

        tr = $("<tr>");
        td = $("<code>").addClass(statuses[val][1]).html(statuses[val][0]);

        $("<td>").html(td).appendTo(tr);
        $("<td>").addClass("text-right").html(""+(new Date()).format("%m/%d/%Y %H:%M")).appendTo(tr);

        table.prepend(tr);
    }
});
