import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_c25027879950fa823e69c28e597137b3
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('../omsserver/Flow/Start?flow_instance_id="+mapRow.get("FLOW_INSTANCE_ID")+"');";return url;

    }
}
