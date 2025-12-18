package com.vidura.exam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerResponse {
    public String message;
    public Object data;
    public  ServerStatus status;

    public ServerResponse(Object data){
        this.data = data;
        this.status = ServerStatus.SUCCESS;
    }

    public ServerResponse(Object data,ServerStatus status){
        this.data = data;
        this.status = status;
    }

    public ServerResponse(Exception ex){
        this.message = ex.getMessage();
        this.status = ServerStatus.ERROR;
    }

    public ResponseEntity<ServerResponse> toResponse(){
        if(this.status == ServerStatus.SUCCESS){
            return ResponseEntity.ok(this);
        }else{
            return ResponseEntity.badRequest().body(this);
        }
    }



}
