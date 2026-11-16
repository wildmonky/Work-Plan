import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_2b0e34852bb21796762c40277eb008be
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname=OMS_RISK&&actiontype=edit&pk_id="+mapRow.get("RISK_ID")+"');";
return url;

    }
}
