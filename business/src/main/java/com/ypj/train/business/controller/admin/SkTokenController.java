package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.SkTokenQueryReq;
import com.ypj.train.business.req.SkTokenSaveReq;
import com.ypj.train.business.resp.SkTokenQueryResp;
import com.ypj.train.business.service.SkTokenService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/sk-token")
public class SkTokenController {

    private final SkTokenService skTokenService;

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        skTokenService.removeById(id);
        return new CommonResp<>();
    }

    @PostMapping("save")
    public CommonResp<Object> save(@RequestBody SkTokenSaveReq skTokenSaveReq) {
        skTokenService.save(skTokenSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<SkTokenQueryResp>> queryList(SkTokenQueryReq skTokenQueryReq) {
        PageResp<SkTokenQueryResp> pageResp = skTokenService.queryList(skTokenQueryReq);
        return new CommonResp<>(pageResp);
    }
}
