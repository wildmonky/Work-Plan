import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_9760e07dbb21d1d34536d5e4db624bb9
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname=OMS_JZB&id&actiontype=edit&pk_id="+mapRow.get("ID")+"');";
return url;

    }
}
