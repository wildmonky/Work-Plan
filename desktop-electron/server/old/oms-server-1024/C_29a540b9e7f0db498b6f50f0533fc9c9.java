import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_29a540b9e7f0db498b6f50f0533fc9c9
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname=PW_DEADNUM_LINE_SET&id&actiontype=edit&pk_id="+mapRow.get("DEADNUM_SET_ID")+"');";
return url;

    }
}
