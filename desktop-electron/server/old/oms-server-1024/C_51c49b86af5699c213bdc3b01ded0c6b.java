import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_51c49b86af5699c213bdc3b01ded0c6b
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('../omsserver/Flow/StartFromCopy?flow_instance_id="+mapRow.get("FLOW_INSTANCE_ID")+"');";return url;

    }
}
