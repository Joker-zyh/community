function like(btn,entityType,entityId,targetId){
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"targetId":targetId},
        function (data){
            data = $.parseJSON(data);
            if (data.code === 0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus === 0?'赞':'已赞');
            }else {
                alert(data.msg);
            }
        }
    );
}