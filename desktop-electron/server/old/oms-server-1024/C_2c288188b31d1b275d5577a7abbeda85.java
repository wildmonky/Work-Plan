import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_2c288188b31d1b275d5577a7abbeda85
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname=OMS_ZBBD_CEWEN&id&actiontype=edit&pk_id="+mapRow.get("ZBBD_CEWEN_ID")+"');";
return url;

    }
}
