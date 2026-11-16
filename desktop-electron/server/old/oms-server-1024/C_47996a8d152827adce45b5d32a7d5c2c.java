import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_47996a8d152827adce45b5d32a7d5c2c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname=ZDH_INSPECT&INSPECT_ID&actiontype=view&pk_id="+mapRow.get("INSPECT_ID")+"');";
return url;

    }
}
