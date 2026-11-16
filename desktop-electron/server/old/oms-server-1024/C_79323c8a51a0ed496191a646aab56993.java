import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_79323c8a51a0ed496191a646aab56993
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('SYS_FLOW_INSTANCE','"+mapRow.get("FLOW_INSTANCE_ID")+"','W_YXLYXQ','"+mapRow.get("ID")+"');";
return url;

    }
}
