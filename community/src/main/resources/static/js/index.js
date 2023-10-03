$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题和正文
	let title = $("#recipient-name").val();
	let content = $("#message-text").val();
	//发送异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function (data){
			data = $.parseJSON(data)
			//显示提示信息
			$("#hintBody").text(data.msg)
			//显示提示框
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//若成功，刷新页面
				if (data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);


}