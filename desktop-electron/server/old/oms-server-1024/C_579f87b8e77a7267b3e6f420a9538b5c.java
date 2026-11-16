import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_579f87b8e77a7267b3e6f420a9538b5c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('SYS_FLOW_INSTANCE','"+mapRow.get("FLOW_INSTANCE_ID")+"','W_YYSQ','"+mapRow.get("ID")+"');";
return url;

    }
}
