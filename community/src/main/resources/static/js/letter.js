$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	let toName = $("#recipient-name").val();
	let content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/add",
		{"toName":toName,"content":content},
		function (data){
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 100);
		}
	)


}

function delete_msg() {
	// TODO 删除数据
	var btn = this;
	var id = $(btn).prev().val();
	$.post(
		CONTEXT_PATH + "/letter/delete",
		{"id":id},
		function(data) {
			data = $.parseJSON(data);
			if(data.code == 0) {
				$(btn).parents(".media").remove();
			} else {
				alert(data.msg);
			}
		}
	);

}