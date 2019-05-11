var replyManager = (function () {
    var getAll = function (obj, callback) {
        console.log("get All....");

        $.getJSON('/replies/'+obj, callback);
    };

    /**
     * 댓글 추가 POST
     * @param obj
     * @param callback
     */
    var add = function (obj, callback) {
        console.log("add....");

        $.ajax({
            type:'post',
            url: '/replies/'+ obj.bno,
            dataType:'json',
            data:JSON.stringify(obj),
            // 전달된 csrf 객체를 처리
            // 이 코드를 이용해서 실제로 댓글을 추가하면 브라우저는 서버에 'X-CSRF-TOKEN' 헤더를 추가한 상태에서 전송하게 됨
            beforeSend : function(xhr){
                xhr.setRequestHeader(obj.csrf.headerName, obj.csrf.token);
            },
            contentType: "application/json",
            success:callback
        });
    };

    /**
     * 댓글 수정 PUT
     * @param obj
     * @param callback
     */
    var update = function (obj, callback) {
        console.log("update......");

        $.ajax({
            type:'put',
            url: '/replies/'+ obj.bno,
            dataType:'json',
            data: JSON.stringify(obj),
            contentType: "application/json",
            // 전달된 csrf 객체를 처리
            // 이 코드를 이용해서 실제로 댓글을 추가하면 브라우저는 서버에 'X-CSRF-TOKEN' 헤더를 추가한 상태에서 전송하게 됨
            beforeSend : function(xhr){
                xhr.setRequestHeader(obj.csrf.headerName, obj.csrf.token);
            },
            success:callback
        });
    }

    /**
     * 댓글 삭제 DELETE
     * @param obj
     * @param callback
     */
    var remove = function (obj, callback) {
        console.log("remove.......");

        $.ajax({
            type:'delete',
            url: '/replies/'+ obj.bno+"/" + obj.rno,
            dataType:'json',
            // 전달된 csrf 객체를 처리
            // 이 코드를 이용해서 실제로 댓글을 추가하면 브라우저는 서버에 'X-CSRF-TOKEN' 헤더를 추가한 상태에서 전송하게 됨
            beforeSend : function(xhr){
                xhr.setRequestHeader(obj.csrf.headerName, obj.csrf.token);
            },
            success:callback
        });
    }

    return {
        getAll: getAll,
        add: add,
        update: update,
        remove: remove
    }
})();