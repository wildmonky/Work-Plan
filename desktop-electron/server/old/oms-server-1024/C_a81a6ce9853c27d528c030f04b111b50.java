import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_a81a6ce9853c27d528c030f04b111b50
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String pdname = StringUtils.isNotBlank(mapRow.get("ID"))? "edit" : "add"String url="javascript:window.open('dataadd.html?dataname="+mapRow.get("OMS_JZB_EDIT")+"&&actiontype='+pdname+'&pk_id="+mapRow.get("ID")+"');";
return url;

    }
}
