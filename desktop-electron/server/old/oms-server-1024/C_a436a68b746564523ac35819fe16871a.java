import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_a436a68b746564523ac35819fe16871a
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('SYS_FLOW_INSTANCE','"+mapRow.get("FLOW_INSTANCE_ID")+"','W_XLZHZJXD','"+mapRow.get("ID")+"');";
return url;

    }
}
