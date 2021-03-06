package com.bigbata.craftsman.api.system;

import com.bigbata.craftsman.api.system.service.MenusService;
import com.bigbata.craftsman.dao.model.SysMenusCheck;
import com.bigbata.craftsman.dao.model.SysMenusEntity;
import com.bigbata.craftsman.dao.model.SysRoleEntity;
import com.bigbata.craftsman.dao.model.SysRoleMenuEntity;
import com.bigbata.craftsman.dao.system.SysRoleDao;
import com.bigbata.craftsman.dao.system.SysRoleMenuDao;
import com.bigbata.craftsman.dao.system.SysMenuDao;
import com.bigbata.craftsman.exception.ME;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lixianghui on 15-5-11.
 */
@RestController
@RequestMapping({"/api/system/roles"})
public class SysRoleApi {
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;
    @Autowired
    private MenusService menusService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<SysRoleEntity> index(@PageableDefault Pageable page, String name) {
        if (name == null)
            name = "%";
        else
            name = "%" + name + "%";
        return sysRoleDao.findByNameLike(page, name);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SysRoleEntity show(@PathVariable Integer id) {
        return sysRoleDao.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public SysRoleEntity create(@RequestBody SysRoleEntity role) {
        if (sysRoleDao.findByName(role.getName()) != null) {
            throw new ME("角色已存在");
        }
        return sysRoleDao.save(role);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void edit(@RequestBody SysRoleEntity role) {
        SysRoleEntity source = sysRoleDao.findOne(role.getId());
        if (!source.getName().equals(role.getName())) {
            if (sysRoleDao.findByName(role.getName()) != null) {
                throw new ME("角色已存在");
            }
        }
        sysRoleDao.save(role);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void destory(@PathVariable Integer id) {
        sysRoleMenuDao.deleteByRoleId(id);
        sysRoleDao.delete(id);
    }

    @RequestMapping(value = "/{roleId}/menus", method = RequestMethod.GET)
    public List<SysMenusCheck> showMenus(@PathVariable Integer roleId) {
        //所有菜单
        Iterable<SysMenusEntity> menus = sysMenuDao.findAll();
        //角色菜单
        List<SysRoleMenuEntity> roleMenus = sysRoleMenuDao.findByRoleid(roleId);
        //转换为checkMenus
        List<SysMenusCheck> checkList = menusService.getCheckList(menus, roleMenus);
        //转化为层叠结构
        return menusService.getNestList(checkList);
    }


    @RequestMapping(value = "/{roleId}/menus", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateMenus(@RequestBody RoleMenusParam param) {
        sysRoleMenuDao.deleteByRoleId(param.getRoleid());
        for (Integer menu : param.getMenus()) {
            SysRoleMenuEntity roleMenu = new SysRoleMenuEntity();
            roleMenu.setRoleid(param.getRoleid());
            roleMenu.setMenusid(menu);
            sysRoleMenuDao.save(roleMenu);
        }
    }

    public static class RoleMenusParam {
        private Integer roleid;

        public Integer getRoleid() {
            return roleid;
        }

        public void setRoleid(Integer roleid) {
            this.roleid = roleid;
        }

        public List<Integer> getMenus() {
            return menus;
        }

        public void setMenus(List<Integer> menus) {
            this.menus = menus;
        }

        private List<Integer> menus;


    }

}
