import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_ca6c039f565bb2bb23a7564dfdc62130
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname="+mapRow.get("OMS_JZB_EDIT")+"&&actiontype=StringUtils.isNotBlank(mapRow.get(ID)) ? "edit" : "add"&pk_id="+mapRow.get("ID")+"');";
return url;

    }
}
