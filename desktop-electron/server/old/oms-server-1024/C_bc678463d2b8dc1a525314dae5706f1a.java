import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_bc678463d2b8dc1a525314dae5706f1a
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "1".equals(mapRow.get("MODIFY_FLAG_MONTH"))?"<font color=red>"+mapRow.get("MONTH_OCS_DL")+"</font>":mapRow.get("MONTH_OCS_DL");
    }
}
